package com.paypulse.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Money Value Object.
 * 
 * Testing patterns (JUnit 5 vs C# xUnit):
 * - @Test is identical to [Fact] in xUnit.
 * - assertThrows(Class, Executable) matches Assert.Throws<T>(Action).
 * - assertEquals(expected, actual) is the standard equality assertion.
 */
class MoneyTest {

    @Test
    @DisplayName("Should normalize scale to 4 decimal places with Banker's Rounding")
    void shouldNormalizeScale() {
        Money money = Money.of("100.5", Currency.USD);

        assertEquals(new BigDecimal("100.5000"), money.amount());
        assertEquals(Currency.USD, money.currency());
    }

    @Test
    @DisplayName("Should correctly add amounts with the same currency")
    void shouldAddMatchingCurrency() {
        Money m1 = Money.of("100.2500", Currency.USD);
        Money m2 = Money.of("50.7500", Currency.USD);

        Money result = m1.add(m2);

        assertEquals(Money.of("151.0000", Currency.USD), result);
    }

    @Test
    @DisplayName("Should correctly subtract amounts with the same currency")
    void shouldSubtractMatchingCurrency() {
        Money m1 = Money.of("200.0000", Currency.EUR);
        Money m2 = Money.of("75.5000", Currency.EUR);

        Money result = m1.subtract(m2);

        assertEquals(Money.of("124.5000", Currency.EUR), result);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when adding mismatched currencies")
    void shouldThrowOnMismatchedCurrencyAddition() {
        Money usd = Money.of("100.0000", Currency.USD);
        Money eur = Money.of("100.0000", Currency.EUR);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usd.add(eur));
        assertTrue(ex.getMessage().contains("Currency mismatch"));
    }

    @Test
    @DisplayName("Should throw NullPointerException when amount or currency is null")
    void shouldThrowOnNullComponents() {
        assertThrows(NullPointerException.class, () -> new Money(null, Currency.USD));
        assertThrows(NullPointerException.class, () -> new Money(BigDecimal.TEN, null));
    }

    @Test
    @DisplayName("Java Record should provide value-based equality and hash code")
    void shouldSupportValueBasedEquality() {
        Money first = Money.of("42.00", Currency.GBP);
        Money second = Money.of("42.0000", Currency.GBP);

        // Value equality: amounts normalized to same scale should be equal
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    @DisplayName("Should evaluate comparison methods accurately")
    void shouldEvaluateComparisons() {
        Money low = Money.of("10.0000", Currency.USD);
        Money high = Money.of("50.0000", Currency.USD);
        Money zero = Money.zero(Currency.USD);

        assertTrue(high.isGreaterThan(low));
        assertFalse(low.isGreaterThan(high));
        assertTrue(low.isPositive());
        assertTrue(zero.isZero());
    }
}
