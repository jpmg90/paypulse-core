package com.paypulse.domain.exception;

import java.util.UUID;

public class AccountInactiveException extends RuntimeException {
    public AccountInactiveException(UUID accountId) {
        super("Account with ID " + accountId + " is not active.");
    }
}