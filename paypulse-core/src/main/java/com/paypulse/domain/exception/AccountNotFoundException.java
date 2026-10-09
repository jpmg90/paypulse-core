package com.paypulse.domain.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(UUID accountId) {
        super("Account with ID " + accountId + " was not found.");
    }
}