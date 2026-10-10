package com.paypulse.api.controller;

import com.paypulse.application.dto.TransferRequest;
import com.paypulse.application.dto.TransferResponse;
import com.paypulse.application.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final PaymentService paymentService;

    public TransferController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> executeTransfer(
            // Transport-layer Idempotency Key: Required by default (required = true)
            // Used by API gateways, proxies, and client retry policies.
            @RequestHeader("Idempotency-Key") String idempotencyHeader,
            
            // Domain-layer Payload: Contains the core business transaction data.
            @Valid @RequestBody TransferRequest request) {

        // Architectural Safeguard: Validate that the transport-layer header matches 
        // the domain payload's idempotency key. This prevents client-side SDK bugs 
        // where a network retry header gets out of sync with the request body data.
        if (!idempotencyHeader.equals(request.idempotencyKey())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, 
                    "Idempotency-Key header must match the idempotencyKey field in the request body."
            );
        }

        // Delegate to the application service layer
        TransferResponse response = paymentService.processTransfer(request);

        return ResponseEntity
                .created(URI.create("/api/v1/transfers/" + response.transactionId()))
                .body(response);
    }
}