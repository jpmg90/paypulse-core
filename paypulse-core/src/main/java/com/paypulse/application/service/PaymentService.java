package com.paypulse.application.service;

import org.springframework.stereotype.Service;

import com.paypulse.infrastructure.persistence.*;
import com.paypulse.application.dto.*;
import com.paypulse.domain.model.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    
    // Declare Dependencies (Repositories, Services, etc.)
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public PaymentService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public TransferResponse processTransfer(TransferRequest request)
    {
        // 1. Idempoency Check -> If transaction with the same idempotency key exists, return the existing transaction response
        // Create a new IdempotencyKey object from the request's idempotency key
        IdempotencyKey idempotencyKey = new IdempotencyKey(request.idempotencyKey());

        Optional<Transaction> existingTransaction = transactionRepository.findByIdempotencyKey(idempotencyKey);

        if (existingTransaction.isPresent()) {
            Transaction transaction = existingTransaction.get();
            return new TransferResponse(
                transaction.getId(),
                transaction.getIdempotencyKey().value(),
                transaction.getTransactionStatus(),
                transaction.getCreatedAt()
            );
        }

        // 2. Account Validation -> Check if source and destination accounts exist, and if the source account has sufficient balance
        Optional<Account> sourceAccount = accountRepository.findById(request.sourceAccountId());
        Optional<Account> destinationAccount = accountRepository.findById(request.destinationAccountId());

        // Check if Empty
        if (sourceAccount.isEmpty() ) {
            throw new IllegalArgumentException("Source account does not exist.");
        }
        if (destinationAccount.isEmpty()) {
            throw new IllegalArgumentException("Destination account does not exist.");
        }

        // Check account Status's
        if (!sourceAccount.get().isActive()) {
            throw new IllegalArgumentException("Source account is not active.");
        }
        if (!destinationAccount.get().isActive()) {
            throw new IllegalArgumentException("Destination account is not active.");
        }
        
        // TODO: Check if source account has sufficient balance
        // NOTE: This can be done by querying the ledger entries for the source account and calculating the balance, or by maintaining a balance field in the Account entity.

        // Check Currency Consistency
        if (!sourceAccount.get().getCurrency().equals(request.currency()) || !destinationAccount.get().getCurrency().equals(request.currency())) {
            throw new IllegalArgumentException("Currency mismatch between accounts and transfer request.");
        }

        // 3. Constructe Domain transaction
        Transaction transaction = new Transaction(
            UUID.randomUUID(),
            idempotencyKey,
            request.description(),
            TransactionStatus.PENDING,
            Instant.now()
        );

        transaction.addLedgerEntry(new LedgerEntry(
            UUID.randomUUID(),
            sourceAccount.get().getId(),
            EntryType.DEBIT,
            Money.of(request.amount().toString(), request.currency()),
            Instant.now()
        ));
        transaction.addLedgerEntry(new LedgerEntry(
            UUID.randomUUID(),
            destinationAccount.get().getId(),
            EntryType.CREDIT,
            Money.of(request.amount().toString(), request.currency()),
            Instant.now()
        ));

        // 4. Validate and save
        if(!transaction.isBalanced()) {
            throw new IllegalStateException("Transaction is not balanced.");
        }

        // Explicatly set the transaction status to POSTED before saving, this is a simplification for this example. In a real-world scenario, you would have a more complex state machine to handle transaction states. (see ADR-001) 
        transaction.setTransactionStatus(TransactionStatus.POSTED);
        transactionRepository.save(transaction);

        // 5. Return response
        return new TransferResponse(
            transaction.getId(),
            transaction.getIdempotencyKey().value(),
            transaction.getTransactionStatus(),
            transaction.getCreatedAt()
        );
    }
}
