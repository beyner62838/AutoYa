package com.example.back_AutoYa.controller;

import com.example.back_AutoYa.Entities.Payment;
import com.example.back_AutoYa.dto.PaymentDTO;
import com.example.back_AutoYa.dto.PaymentIntentRequest;
import com.example.back_AutoYa.dto.ReservationDTO;
import com.example.back_AutoYa.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.example.back_AutoYa.utils.ReceiptGenerator;
import com.example.back_AutoYa.service.EmailService;
import com.example.back_AutoYa.repository.ReservationRepository;
import com.example.back_AutoYa.Entities.Reservation;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReservationRepository reservationRepository;


    private final PaymentService paymentService;



    @GetMapping
    public List<PaymentDTO> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{userId}")
    public List<PaymentDTO> getAllReservationsById(@PathVariable Long userId) {
        return paymentService.getAllPaymentsById(userId);
    }

    @PostMapping()
    public Payment createPayment(
            @RequestParam Long reservationId,
            @RequestParam Double amount,
            @RequestParam String method ) {
        return paymentService.createPayment(reservationId, amount, method);
    }

    // ----------- Corregido: ahora recibe el request con datos ----------
    @PostMapping("/intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody PaymentIntentRequest request) {
        Map<String, Object> response = paymentService.createPaymentIntent(
                request.reservationId(),
                request.amount(),
                request.method()
        );
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

    // ----------- 🚀 Confirmación de pago ----------
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(
            @RequestParam Long reservationId,
            @RequestParam Double amount,
            @RequestParam String method,
            @RequestParam String intentId) {

        // 1️⃣ Validar que el intent exista y esté capturado o completado
        Map<String, Object> intentStatus = paymentService.getIntentStatus(intentId);

        if (intentStatus.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", "No se pudo capturar el intent o no existe.")
            );
        }

        // Obtener el estado del intento de pago
        String status = (String) intentStatus.get("status");

        // ✅ Permitir CAPTURED o COMPLETED
        if (!"CAPTURED".equalsIgnoreCase(status) && !"COMPLETED".equalsIgnoreCase(status)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", "El intent no está en un estado válido para confirmar el pago.")
            );
        }

        // 2️⃣ Crear el pago real en BD
        Payment payment = paymentService.createPayment(reservationId, amount, method);

        // -> Generar recibo HTML y enviar por correo si es posible
        try {
            Reservation res = reservationRepository.findById(reservationId).orElse(null);
            if (res != null) {
                String html = ReceiptGenerator.generateHtml(res);
                // guardar archivo html localmente para pruebas
                try {
                    java.nio.file.Path receiptsDir = java.nio.file.Paths.get("/tmp/autoya/receipts");
                    java.nio.file.Files.createDirectories(receiptsDir);
                    java.nio.file.Files.writeString(receiptsDir.resolve("receipt-" + reservationId + ".html"), html);
                } catch (Exception ex) {
                    // no interrumpir si falla el guardado
                    System.out.println("No se pudo guardar HTML: " + ex.getMessage());
                }
                if (res.getClient() != null && res.getClient().getEmail() != null && !res.getClient().getEmail().isEmpty()) {
                    try {
                        emailService.sendHtmlEmail(res.getClient().getEmail(), "Recibo AutoYa - Reserva " + reservationId, html);
                    } catch (Exception me) {
                        System.out.println("No se pudo enviar email: " + me.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error generando/enviando recibo: " + e.getMessage());
        }


        // 3️⃣ Retornar información del pago y el intent
        return ResponseEntity.ok(Map.of(
                "payment", payment,
                "intentId", intentId,
                "status", status,
                "message", "Pago confirmado y registrado en el sistema."
        ));
    }

}