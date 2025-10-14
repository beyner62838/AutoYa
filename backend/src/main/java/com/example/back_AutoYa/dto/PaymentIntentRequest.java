package com.example.back_AutoYa.dto;

public record PaymentIntentRequest(
        Long reservationId,
        Double amount,
        String method
) {}
