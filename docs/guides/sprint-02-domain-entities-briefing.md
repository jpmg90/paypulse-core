# 📋 Sprint Block 2 Briefing: Double-Entry Ledger Entities

> **Role:** You are the Developer (hands on the keyboard). Antigravity is your Senior Architect / Tech Lead.  
> **Goal:** Draft the core domain entities for `PayPulse-Core` in Java, run tests, commit your work, and submit for an architectural code review.

---

## 🎯 Domain Requirements (Double-Entry Ledger)

In financial systems, money is never simply "updated" in place. Every movement of funds is recorded as an immutable set of balanced ledger entries:

1. **`Account`**:
   * Attributes: `id` (`UUID`), `accountNumber` (`String`), `type` (`AccountType` enum: `ASSET`, `LIABILITY`, `EQUITY`, `REVENUE`, `EXPENSE`), `currency` (`Currency`), `status` (`AccountStatus` enum: `ACTIVE`, `FROZEN`, `CLOSED`).
   * Architectural Note: In C#, you'd use properties `{ get; set; }`. In Java, write private fields with getters/setters or use Lombok (`@Getter`, `@Setter`), or start with pure Java methods.

2. **`LedgerEntry`**:
   * Represents one leg of a financial transaction.
   * Attributes: `id` (`UUID`), `accountId` (`UUID`), `type` (`EntryType` enum: `DEBIT`, `CREDIT`), `amount` (`BigDecimal` or `Money`), `createdAt` (`Instant`).
   * Rule: A ledger entry is immutable once created.

3. **`Transaction`**:
   * Represents the business transfer event.
   * Attributes: `id` (`UUID`), `idempotencyKey` (`IdempotencyKey` or `String`), `description` (`String`), `entries` (`List<LedgerEntry>`), `status` (`TransactionStatus`: `PENDING`, `POSTED`, `FAILED`), `createdAt` (`Instant`).
   * **The Golden Rule:** $\sum \text{Debits} = \sum \text{Credits}$. The transaction must have a method (e.g., `isBalanced()`) verifying that total debits equal total credits.

---

## 📂 Where to Put Your Code

Create your new files under:
```text
paypulse-core/src/main/java/com/paypulse/domain/model/
├── Account.java
├── AccountType.java
├── AccountStatus.java
├── EntryType.java
├── LedgerEntry.java
├── Transaction.java
└── TransactionStatus.java
```

---

## 💡 Quick Tips for the C# -> Java Transition

1. **Dates/Timestamps:** Use `java.time.Instant` instead of `DateTime.UtcNow`.
2. **Lists:** In Java, declare interfaces: `List<LedgerEntry> entries = new ArrayList<>();` (import `java.util.List` and `java.util.ArrayList`).
3. **UUIDs:** `UUID.randomUUID()` works just like `Guid.NewGuid()`.
4. **Compile & Test Command:**
   ```powershell
   cd paypulse-core
   .\mvnw.cmd compile
   ```
