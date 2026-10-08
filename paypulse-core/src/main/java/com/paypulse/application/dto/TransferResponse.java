package com.paypulse.application.dto;

import com.paypulse.domain.model.TransactionStatus;
import java.time.Instant;
import java.util.UUID;

public record TransferResponse(
    UUID transactionId,
    String idempotencyKey,
    TransactionStatus status,
    Instant createdAt
) {}