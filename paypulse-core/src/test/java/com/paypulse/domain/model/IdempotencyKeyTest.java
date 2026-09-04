package com.paypulse.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdempotencyKeyTest {

    @Test
    @DisplayName("Should trim and accept valid idempotency keys")
    void shouldAcceptValidKey() {
        IdempotencyKey key = IdempotencyKey.of("  idemp-12345  ");
        assertEquals("idemp-12345", key.value());
    }

    @Test
    @DisplayName("Should reject null or blank keys")
    void shouldRejectInvalidKeys() {
        assertThrows(NullPointerException.class, () -> IdempotencyKey.of(null));
        assertThrows(IllegalArgumentException.class, () -> IdempotencyKey.of("   "));
        assertThrows(IllegalArgumentException.class, () -> IdempotencyKey.of(""));
    }

    @Test
    @DisplayName("Should generate random UUID-based keys")
    void shouldGenerateUniqueKeys() {
        IdempotencyKey k1 = IdempotencyKey.generate();
        IdempotencyKey k2 = IdempotencyKey.generate();

        assertNotNull(k1.value());
        assertNotEquals(k1, k2);
    }
}
