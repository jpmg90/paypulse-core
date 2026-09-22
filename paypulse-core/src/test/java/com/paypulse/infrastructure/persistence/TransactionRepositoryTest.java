package com.paypulse.infrastructure.persistence;

import com.paypulse.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TransactionRepositoryTest {
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @DisplayName("Should save transaction and cascade persist its ledger entries")
    void shouldPersistTransactionAndEntries(){
        // 1. Create IdempotencyKey and Tranaction
        IdempotencyKey idempotencyKey = IdempotencyKey.generate();
        Transaction transaction = new Transaction(UUID.randomUUID(), idempotencyKey, "Transfer funds", TransactionStatus.PENDING, Instant.now());

        // 2. Add balanced Entries
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), sourceAccountId, EntryType.DEBIT, Money.of("50.00", Currency.USD), Instant.now()));
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), destinationAccountId, EntryType.CREDIT, Money.of("50.00", Currency.USD), Instant.now()));

        // 3. Save via Repository
        transactionRepository.save(transaction);

        // 4. Query back using derived query on embedded IdempotencyKey
        Optional<Transaction> retrievedTransaction = transactionRepository.findByIdempotencyKey(idempotencyKey);

        // 5. Assertions
        assertTrue(retrievedTransaction.isPresent(), "Transaction should be present");
        assertEquals(2, retrievedTransaction.get().getLedgerEntries().size(), "Transaction should have 2 ledger entries");
        assertTrue(retrievedTransaction.get().isBalanced());
    }
}
