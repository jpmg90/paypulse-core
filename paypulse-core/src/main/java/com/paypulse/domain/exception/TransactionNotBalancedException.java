package com.paypulse.domain.exception;

public class TransactionNotBalancedException extends RuntimeException {
    public TransactionNotBalancedException(String message) {
        super(message);
    }
}