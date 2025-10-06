package com.example.back_AutoYa.controller;

import com.example.back_AutoYa.dto.PaymentDTO;
import com.example.back_AutoYa.dto.PaymentIntentRequest;
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

    // 🔹 Obtener todos los pagos guardados en BD
    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<PaymentDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    // 🔹 Crear un pago directo (sin intent)
    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(
            @RequestParam Long reservationId,
            @RequestParam Double amount,
            @RequestParam String method) {

        PaymentDTO paymentDTO = paymentService.createPayment(reservationId, amount, method);
        return ResponseEntity.ok(paymentDTO);
    }

    // 🔹 Crear un intent de pago temporal (no persistente)
    @PostMapping("/intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody PaymentIntentRequest request) {
        Map<String, Object> response = paymentService.createPaymentIntent(
                request.reservationId(),
                request.amount(),
                request.method()
        );
        return ResponseEntity.ok(response);
    }

    // 🔹 Capturar un intent (guardar en memoria y luego persistir)
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

    // 🔹 Confirmar pago (verifica intent antes de crear el pago real)
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(
            @RequestParam Long reservationId,
            @RequestParam Double amount,
            @RequestParam String method,
            @RequestParam String intentId) {

        // 1️⃣ Validar intent
        Map<String, Object> intentStatus = paymentService.getIntentStatus(intentId);

        if (intentStatus.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", "No se pudo capturar el intent o no existe.")
            );
        }

        // 2️⃣ Validar estado
        String status = (String) intentStatus.get("status");
        if (!"CAPTURED".equalsIgnoreCase(status) && !"COMPLETED".equalsIgnoreCase(status)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", "El intent no está en un estado válido para confirmar el pago.")
            );
        }

        // 3️⃣ Crear el pago real
        PaymentDTO paymentDTO = paymentService.createPayment(reservationId, amount, method);

        // 4️⃣ Responder
        return ResponseEntity.ok(Map.of(
                "payment", paymentDTO,
                "intentId", intentId,
                "status", status,
                "message", "Pago confirmado y registrado en el sistema."
        ));
    }
}
