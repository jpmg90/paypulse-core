package com.paypulse.domain.model;
import java.util.UUID; 
import java.time.Instant;

public class LedgerEntry {
    private UUID id;
    private UUID accountId;
    private EntryType entryType;
    private Money amount;
    private Instant createdAt;

    // zero-argument constructor for frameworks that require it (e.g., JPA, Jackson)
    protected LedgerEntry() {
    }

    public LedgerEntry(UUID id, UUID accountId, EntryType entryType, Money amount, Instant createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.entryType = entryType;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getId() {
        return id;
    }
    public EntryType getEntryType() {
        return entryType;
    }
    public UUID getAccountId() {
        return accountId;
    }
    public Money getAmount() {
        return amount;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }

}

