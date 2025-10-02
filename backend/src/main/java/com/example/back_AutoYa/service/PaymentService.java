package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.Enums.PaymentMethod;
import com.example.back_AutoYa.Entities.Enums.PaymentStatus;
import com.example.back_AutoYa.Entities.Enums.ReservationStatus;
import com.example.back_AutoYa.Entities.Payment;
import com.example.back_AutoYa.Entities.Reservation;
import com.example.back_AutoYa.repository.PaymentRepository;
import com.example.back_AutoYa.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PaymentService {

    // Simulación de intents de pago en memoria asociados a Payment
    private final Map<String, Payment> paymentIntents = new ConcurrentHashMap<>();

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final CompletionService completionService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 🔹 Crear intent asociado a una reserva
    public Map<String, Object> createPaymentIntent(Long reservationId, Double amount, String method) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + reservationId));

        String intentId = UUID.randomUUID().toString();

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setMethod(PaymentMethod.valueOf(method));
        payment.setStatus(PaymentStatus.PENDING); // Intent aún no confirmado
        payment.setDate(LocalDate.now());

        // Guardamos el intent en memoria (no en BD todavía)
        paymentIntents.put(intentId, payment);

        return Map.of(
                "intentId", intentId,
                "status", "CREATED",
                "reservationId", reservationId,
                "message", "Intent de pago creado y asociado a la reserva."
        );
    }

    // 🔹 Capturar intent de pago → convertir en Payment real
    @Transactional
    public Map<String, Object> capturePayment(String intentId) {
        if (intentId == null || !paymentIntents.containsKey(intentId)) {
            return Map.of("error", "Intent de pago no encontrado.");
        }

        Payment payment = paymentIntents.get(intentId);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            return Map.of(
                    "intentId", intentId,
                    "status", payment.getStatus(),
                    "message", "El intent ya fue capturado o cancelado."
            );
        }

        // Actualizamos estado y lo persistimos en BD
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        // Actualizar estado de la reserva si ya está cubierta
        Reservation reservation = payment.getReservation();
        double totalPaid = reservation.getPayments().stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Payment::getAmount)
                .sum();

        if (totalPaid >= reservation.getTotalPrice()) {
            reservation.setStatus(ReservationStatus.RESERVED);
            reservationRepository.save(reservation);
        }

        return Map.of(
                "intentId", intentId,
                "status", "CAPTURED",
                "reservationId", reservation.getId(),
                "message", "Pago capturado y asociado correctamente."
        );
    }

    // -------- Métodos ya existentes (sin modificar) --------
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Transactional
    public Payment createPayment(Long reservationId, Double amount, String method) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + reservationId));

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setMethod(PaymentMethod.valueOf(method));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setDate(LocalDate.now());

        paymentRepository.save(payment);

        completionService.completePayment(payment.getId());

        return payment;
    }

    @Async
    @Transactional
    public void processPaymentAsync(Long paymentId) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);

            Reservation reservation = payment.getReservation();
            double totalPaid = reservation.getPayments().stream()
                    .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                    .mapToDouble(Payment::getAmount)
                    .sum();

            if (totalPaid >= reservation.getTotalPrice()) {
                reservation.setStatus(ReservationStatus.RESERVED);
                reservationRepository.save(reservation);
            }

        }, 1, TimeUnit.MINUTES);
    }
}
