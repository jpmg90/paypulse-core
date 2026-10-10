package com.paypulse.application.dto;

import com.paypulse.domain.model.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
    @NotBlank(message = "Idempotency key is required")
    String idempotencyKey,
    
    @NotNull(message = "Source account ID is required")
    UUID sourceAccountId,
    
    @NotNull(message = "Destination account ID is required")
    UUID destinationAccountId,
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    BigDecimal amount,
    
    @NotNull(message = "Currency is required")
    Currency currency,
    
    String description
) {}