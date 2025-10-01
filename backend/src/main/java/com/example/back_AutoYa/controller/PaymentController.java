package com.example.back_AutoYa.controller;

import com.example.back_AutoYa.Entities.Payment;
import com.example.back_AutoYa.dto.ReservationDTO;
import com.example.back_AutoYa.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
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


    @PostMapping("/intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> response = paymentService.createPaymentIntent();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/capture")
    public ResponseEntity<Map<String, Object>> capturePayment(@RequestBody Map<String, Object> request) {
        String intentId = (String) request.get("intentId");
        Map<String, Object> response = paymentService.capturePayment(intentId);
        if (response.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }
}
