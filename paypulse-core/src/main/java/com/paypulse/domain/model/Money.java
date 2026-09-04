package com.paypulse.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable Domain Value Object representing monetary amounts.
 * 
 * Conceptual Mapping for C# Engineers:
 * - In C#: Similar to `public readonly record struct Money(decimal Amount, Currency Currency)`
 * - In Java 17+: `record` produces a final class extending `java.lang.Record`,
 *   with private final fields, accessor methods (e.g., `amount()`, not `getAmount()`),
 *   value-based `equals()`, `hashCode()`, and `toString()`.
 */
public record Money(BigDecimal amount, Currency currency) {

    public static final int DEFAULT_SCALE = 4;
    public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN; // Banker's Rounding

    /**
     * Compact Constructor:
     * Unique to Java records. Runs before the canonical constructor assigns fields.
     * Ideal for validation and normalization without duplicating field assignments.
     */
    public Money {
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(currency, "Currency must not be null");

        // Normalize scale to 4 decimal places using Banker's Rounding (standard for financial ledgers)
        amount = amount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    public static Money of(String amountStr, Currency currency) {
        return new Money(new BigDecimal(amountStr), currency);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    private void validateSameCurrency(Money other) {
        Objects.requireNonNull(other, "Operand Money must not be null");
        if (this.currency != other.currency) {
            throw new IllegalArgumentException(
                "Currency mismatch: Cannot operate between " + this.currency + " and " + other.currency
            );
        }
    }
}
