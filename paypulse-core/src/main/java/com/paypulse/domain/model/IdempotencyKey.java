package com.paypulse.domain.model;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.*;

/**
 * Strongly typed Value Object representing an Idempotency Key.
 * 
 * Prevents stringly-typed anti-patterns in payment processing.
 * Guarantees that the key is non-blank and safe for database indexing.
 */
@Embeddable
public record IdempotencyKey(
    @Column(name = "idempotency_key", nullable = false, length = 128, unique = true)
    String value) {

    private static final int MAX_KEY_LENGTH = 128;

    public IdempotencyKey {
        Objects.requireNonNull(value, "Idempotency key must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Idempotency key must not be blank");
        }
        if (trimmed.length() > MAX_KEY_LENGTH) {
            throw new IllegalArgumentException("Idempotency key exceeds maximum length of " + MAX_KEY_LENGTH);
        }
        value = trimmed;
    }

    public static IdempotencyKey generate() {
        return new IdempotencyKey(UUID.randomUUID().toString());
    }

    public static IdempotencyKey of(String key) {
        return new IdempotencyKey(key);
    }
}
