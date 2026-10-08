package com.paypulse.application.dto;

import com.paypulse.domain.model.Currency;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
    String idempotencyKey,
    UUID sourceAccountId,
    UUID destinationAccountId,
    BigDecimal amount,
    Currency currency,
    String description
) {}