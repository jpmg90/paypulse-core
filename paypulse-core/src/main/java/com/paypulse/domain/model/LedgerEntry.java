package com.paypulse.domain.model;
import java.util.UUID; 
import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {
    @Id
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private UUID id;
    @Column(name = "account_id", nullable = false)
    private UUID accountId;
    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false)
    private EntryType entryType;
    @Embedded
    private Money amount;
    @Column(name = "created_at", nullable = false)
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

