package com.paypulse.infrastructure.persistence;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paypulse.domain.model.Transaction;
import com.paypulse.domain.model.IdempotencyKey;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    
    // Derived methods
    Optional<Transaction> findByIdempotencyKey(IdempotencyKey idempotencyKey);
}
