package com.paypulse.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Transaction Value Object.
 * 
 * Testing patterns (JUnit 5 vs C# xUnit):
 * - @Test is identical to [Fact] in xUnit.
 * - assertThrows(Class, Executable) matches Assert.Throws<T>(Action).
 * - assertEquals(expected, actual) is the standard equality assertion.
 */
public class TransactionTest {
    
    @Test
    @DisplayName("should create a balanced tranaction")
    void shouldBeBalancedWhenCreditsEquilDebits()
    {
        Transaction transaction = new Transaction(UUID.randomUUID(),  IdempotencyKey.generate(),
                 "Test Transaction for balanced tranactions", TransactionStatus.PENDING, Instant.now());
        
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), UUID.randomUUID(), EntryType.CREDIT, new Money(new BigDecimal(100), Currency.USD), Instant.now()));
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), UUID.randomUUID(), EntryType.DEBIT, new Money(new BigDecimal(100), Currency.USD), Instant.now()));

        assertTrue(transaction.isBalanced());
    }

    @Test
    @DisplayName("should be balanced with multiple credits and debits")
    void ShouldBeBalancedWithMultipleLedgerEntries()
    {        
        Transaction transaction = new Transaction(UUID.randomUUID(),  IdempotencyKey.generate(),
                 "Test Transaction for balanced tranactions with multiple entries", TransactionStatus.PENDING, Instant.now());
        
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), UUID.randomUUID(), EntryType.DEBIT, new Money(new BigDecimal(60), Currency.USD), Instant.now()));
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), UUID.randomUUID(), EntryType.DEBIT, new Money(new BigDecimal(40), Currency.USD), Instant.now()));
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), UUID.randomUUID(), EntryType.CREDIT, new Money(new BigDecimal(100), Currency.USD), Instant.now()));

        assertTrue(transaction.isBalanced());
    }

    @Test
    @DisplayName("should fail balance when credits do not equal debits")
    void shouldFailBalanceWhenCreditsDoNotEqualDebits()
    {        
        Transaction transaction = new Transaction(UUID.randomUUID(),  IdempotencyKey.generate(),
                 "Test Transaction for balanced tranactions with multiple entries", TransactionStatus.PENDING, Instant.now());
        
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), UUID.randomUUID(), EntryType.DEBIT, new Money(new BigDecimal(100), Currency.USD), Instant.now()));
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), UUID.randomUUID(), EntryType.CREDIT, new Money(new BigDecimal(90), Currency.USD), Instant.now()));

        assertFalse(transaction.isBalanced());
    }

    @Test
    @DisplayName("should throw exception when fewer than two entries are present")
    void shouldThrowWhenFewerThanTwoEntriesArePresent()
    {        
        Transaction transaction = new Transaction(UUID.randomUUID(),  IdempotencyKey.generate(),
                 "Test Transaction for balanced tranactions with multiple entries", TransactionStatus.PENDING, Instant.now());
        
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), UUID.randomUUID(), EntryType.DEBIT, new Money(new BigDecimal(100), Currency.USD), Instant.now()));

        assertThrows(IllegalStateException.class, () -> transaction.isBalanced());  
    }

    @Test
    @DisplayName("should throw exception when currencies do not match")
    void shouldThrowWhenCurrenciesDoNotMatch()
    {
        Transaction transaction = new Transaction(UUID.randomUUID(),  IdempotencyKey.generate(),
                 "Test Transaction for balanced tranactions", TransactionStatus.PENDING, Instant.now());
        
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), UUID.randomUUID(), EntryType.CREDIT, new Money(new BigDecimal(100), Currency.USD), Instant.now()));
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), UUID.randomUUID(), EntryType.DEBIT, new Money(new BigDecimal(100), Currency.EUR), Instant.now()));

        assertThrows(IllegalStateException.class, () -> transaction.isBalanced());
    }
    @Test
    @DisplayName("should prevent external mutation of ledger entries")
    void shouldPreventExternalMutationOfLedgerEntries()
    {
        Transaction transaction = new Transaction(UUID.randomUUID(),  IdempotencyKey.generate(),
                 "Test Transaction for balanced tranactions", TransactionStatus.PENDING, Instant.now());
        
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), UUID.randomUUID(), EntryType.CREDIT, new Money(new BigDecimal(100), Currency.USD), Instant.now()));
        transaction.addLedgerEntry(new LedgerEntry(UUID.randomUUID(), UUID.randomUUID(), EntryType.DEBIT, new Money(new BigDecimal(100), Currency.USD), Instant.now()));

        assertThrows(UnsupportedOperationException.class, () -> transaction.getLedgerEntries().add(new LedgerEntry(UUID.randomUUID(), UUID.randomUUID(), EntryType.DEBIT, new Money(new BigDecimal(100), Currency.USD), Instant.now())));
    }
}
