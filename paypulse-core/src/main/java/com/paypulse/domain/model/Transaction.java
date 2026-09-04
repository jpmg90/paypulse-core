package com.paypulse.domain.model;
import java.util.UUID;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Transaction {
    private UUID id;
    private IdempotencyKey idempotencyKey;
    private String description;
    private TransactionStatus transactionStatus;
    private Instant createdAt;
    private List<LedgerEntry> ledgerEntries = new ArrayList<>();

    // Constructor without ledger Entries
    public Transaction(UUID id, IdempotencyKey idempotencyKey, String description, TransactionStatus transactionStatus, Instant createdAt) {
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.description = description;
        this.transactionStatus = transactionStatus;
        this.createdAt = createdAt;
    }

    // Constructor that has ledger Entries
    public Transaction(UUID id, IdempotencyKey idempotencyKey, String description, TransactionStatus transactionStatus, List<LedgerEntry> ledgerEntries, Instant createdAt) {
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.description = description;
        this.transactionStatus = transactionStatus;
        this.createdAt = createdAt;
        this.ledgerEntries = (ledgerEntries != null) ? new ArrayList<>(ledgerEntries) : new ArrayList<>();
    }

    // Getters
    public UUID getId() {
        return id;
    }
    public IdempotencyKey getIdempotencyKey() {
        return idempotencyKey;
    }
    public String getDescription() {
        return description;
    }
    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public List<LedgerEntry> getLedgerEntries() {
        return Collections.unmodifiableList(this.ledgerEntries);
    }

    // Setters
    public void setDescription(String description) {
        this.description = description;
    }
    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    // Helper Methods
    public void addLedgerEntry(LedgerEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("LedgerEntry must not be null");
        }
        this.ledgerEntries.add(entry);
    }

    public boolean isBalanced()
    {
        // Check Empty Ledger Entries
        if (ledgerEntries.isEmpty()) {
            throw new IllegalStateException("No ledger entries to evaluate");
        }

        // Check for at least two ledger entries
        if (ledgerEntries.size() < 2) {
            throw new IllegalStateException("At least two ledger entries are required to evaluate balance");
        }

        // Check that all ledger entries have the same currency
        Currency firstCurrency = ledgerEntries.get(0).getAmount().currency();
        for (LedgerEntry entry : ledgerEntries) {
            if (entry.getAmount().currency() != firstCurrency) {
                throw new IllegalStateException("Ledger entries have different currencies");
            }
        }

        // Calculate total credits and debits 
        Money total = Money.zero(firstCurrency);
        for (LedgerEntry entry : ledgerEntries) {
            if (entry.getEntryType() == EntryType.CREDIT) {
                total = total.add(entry.getAmount());
            } else if (entry.getEntryType() == EntryType.DEBIT) {
                total = total.subtract(entry.getAmount());
            } else {
                throw new IllegalStateException("Unknown entry type: " + entry.getEntryType());
            }
        }

        return total.isZero();
    }
}
