package com.paypulse.application.service;

import com.paypulse.application.dto.TransferRequest;
import com.paypulse.application.dto.TransferResponse;
import com.paypulse.domain.exception.*;
import com.paypulse.domain.model.*;
import com.paypulse.infrastructure.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Account sourceAccount;
    private Account destinationAccount;
    private UUID sourceId;
    private UUID destId;

    @BeforeEach
    void setUp() {
        sourceId = UUID.randomUUID();
        destId = UUID.randomUUID();

        sourceAccount = new Account(sourceId, "ACC-001", AccountType.ASSET, Currency.USD, AccountStatus.ACTIVE);
        destinationAccount = new Account(destId, "ACC-002", AccountType.LIABILITY, Currency.USD, AccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should process successful transfer when accounts valid and balanced")
    void shouldProcessTransferSuccessfully() {
        TransferRequest request = new TransferRequest(
            "idemp-123",
            sourceId,
            destId,
            new BigDecimal("100.00"),
            Currency.USD,
            "Payment for services"
        );

        when(transactionRepository.findByIdempotencyKey(any(IdempotencyKey.class))).thenReturn(Optional.empty());
        when(accountRepository.findById(sourceId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destId)).thenReturn(Optional.of(destinationAccount));

        TransferResponse response = paymentService.processTransfer(request);

        assertNotNull(response);
        assertEquals("idemp-123", response.idempotencyKey());
        assertEquals(TransactionStatus.POSTED, response.status());

        // Verify save was invoked once on repository
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should return existing transaction without re-saving when idempotency key exists")
    void shouldReturnExistingTransactionWhenIdempotentKeyMatches() {
        IdempotencyKey key = new IdempotencyKey("idemp-duplicate");
        Transaction existingTx = new Transaction(
            UUID.randomUUID(),
            key,
            "Original payment",
            TransactionStatus.POSTED,
            Instant.now()
        );

        TransferRequest request = new TransferRequest(
            "idemp-duplicate",
            sourceId,
            destId,
            new BigDecimal("100.00"),
            Currency.USD,
            "Retry payment"
        );

        when(transactionRepository.findByIdempotencyKey(key)).thenReturn(Optional.of(existingTx));

        TransferResponse response = paymentService.processTransfer(request);

        assertEquals(existingTx.getId(), response.transactionId());
        assertEquals("idemp-duplicate", response.idempotencyKey());

        // CRITICAL: Ensure zero saves occurred and accounts were not even queried
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when source account does not exist")
    void shouldThrowWhenSourceAccountNotFound() {
        TransferRequest request = new TransferRequest(
            "idemp-404",
            sourceId,
            destId,
            new BigDecimal("50.00"),
            Currency.USD,
            "Transfer"
        );

        when(transactionRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        when(accountRepository.findById(sourceId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> paymentService.processTransfer(request));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw AccountInactiveException when destination account is suspended")
    void shouldThrowWhenAccountInactive() {
        Account inactiveAccount = new Account(destId, "ACC-002", AccountType.LIABILITY, Currency.USD, AccountStatus.SUSPENDED);

        TransferRequest request = new TransferRequest(
            "idemp-inactive",
            sourceId,
            destId,
            new BigDecimal("50.00"),
            Currency.USD,
            "Transfer"
        );

        when(transactionRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        when(accountRepository.findById(sourceId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destId)).thenReturn(Optional.of(inactiveAccount));

        assertThrows(AccountInactiveException.class, () -> paymentService.processTransfer(request));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CurrencyMismatchException when request currency differs from account")
    void shouldThrowWhenCurrencyMismatched() {
        TransferRequest request = new TransferRequest(
            "idemp-currency-err",
            sourceId,
            destId,
            new BigDecimal("50.00"),
            Currency.EUR, // Account is USD!
            "Transfer"
        );

        when(transactionRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        when(accountRepository.findById(sourceId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destId)).thenReturn(Optional.of(destinationAccount));

        assertThrows(CurrencyMismatchException.class, () -> paymentService.processTransfer(request));
        verify(transactionRepository, never()).save(any());
    }
}