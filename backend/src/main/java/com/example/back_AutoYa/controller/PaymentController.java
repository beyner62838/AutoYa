package com.example.back_AutoYa.controller;

import com.example.back_AutoYa.Entities.Payment;
import com.example.back_AutoYa.dto.ReservationDTO;
import com.example.back_AutoYa.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @PostMapping()
    public Payment createPayment(
            @RequestParam Long reservationId,
            @RequestParam Double amount,
            @RequestParam String method ) {

        return paymentService.createPayment(reservationId, amount, method);
    }
}
