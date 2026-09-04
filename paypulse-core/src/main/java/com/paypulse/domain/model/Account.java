package com.paypulse.domain.model;
import java.util.UUID; 

public class Account {
    private UUID id;
    private String accountNumber;
    private AccountType accountType;
    private Currency currency;
    private AccountStatus accountStatus;

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
