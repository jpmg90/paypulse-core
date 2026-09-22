package com.paypulse.infrastructure.persistence;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.paypulse.domain.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    
    // Derived methods
    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);
}
