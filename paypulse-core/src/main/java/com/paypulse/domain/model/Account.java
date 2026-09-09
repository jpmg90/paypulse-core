package com.paypulse.domain.model;
import java.util.UUID; 
import jakarta.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @Column(name = "id", updatable = false,  nullable = false, unique = true)
    private UUID id;
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus;

    // zero-argument constructor for frameworks that require it (e.g., JPA, Jackson)
    protected Account() {
    }   

    public Account(UUID id, String accountNumber, AccountType accountType, Currency currency, AccountStatus accountStatus) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.currency = currency;
        this.accountStatus = accountStatus;
    }

    // Getters 
    public UUID getId() {
        return id;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public AccountType getAccountType() {
        return accountType;
    }
    public Currency getCurrency() {
        return currency;
    }
    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    // Setters
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    // Helper Method
    public boolean isActive() {
        return this.accountStatus == AccountStatus.ACTIVE;
    }
}
