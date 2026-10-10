package com.paypulse.api.controller;

import tools.jackson.databind.ObjectMapper;
import com.paypulse.application.dto.TransferRequest;
import com.paypulse.application.dto.TransferResponse;
import com.paypulse.application.service.PaymentService;
import com.paypulse.domain.model.Currency;
import com.paypulse.domain.model.TransactionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    @DisplayName("POST /api/v1/transfers should return 201 Created when header and body keys match")
    void shouldReturn201OnMatchingIdempotencyKeys() throws Exception {
        UUID txId = UUID.randomUUID();
        String idempotencyKey = "key-123";

        TransferRequest request = new TransferRequest(
                idempotencyKey,
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                Currency.USD,
                "Transfer test"
        );

        TransferResponse response = new TransferResponse(
                txId,
                idempotencyKey,
                TransactionStatus.POSTED,
                Instant.now()
        );

        when(paymentService.processTransfer(any(TransferRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/transfers")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/transfers/" + txId))
                .andExpect(jsonPath("$.transactionId").value(txId.toString()))
                .andExpect(jsonPath("$.status").value("POSTED"));
    }

    @Test
    @DisplayName("POST /api/v1/transfers should return 400 Bad Request when Idempotency-Key header is missing")
    void shouldReturn400WhenHeaderMissing() throws Exception {
        TransferRequest request = new TransferRequest(
                "key-123",
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                Currency.USD,
                "Transfer test"
        );

        mockMvc.perform(post("/api/v1/transfers")
                        // Omitted .header("Idempotency-Key", ...)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/transfers should return 400 Bad Request when header and body keys do not match")
    void shouldReturn400WhenKeysMismatch() throws Exception {
        TransferRequest request = new TransferRequest(
                "body-key-999", // Mismatched key in body
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                Currency.USD,
                "Transfer test"
        );

        mockMvc.perform(post("/api/v1/transfers")
                        .header("Idempotency-Key", "header-key-111") // Mismatched key in header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/transfers should return 400 Bad Request when amount is negative")
    void shouldReturn400WhenAmountNegative() throws Exception {
        TransferRequest invalidRequest = new TransferRequest(
                "key-123",
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("-50.00"), // Invalid!
                Currency.USD,
                "Invalid transfer"
        );

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}