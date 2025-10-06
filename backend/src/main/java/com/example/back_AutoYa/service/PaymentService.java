package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.Enums.PaymentMethod;
import com.example.back_AutoYa.Entities.Enums.PaymentStatus;
import com.example.back_AutoYa.Entities.Enums.ReservationStatus;
import com.example.back_AutoYa.Entities.Payment;
import com.example.back_AutoYa.Entities.Reservation;
import com.example.back_AutoYa.dto.PaymentDTO;
import com.example.back_AutoYa.repository.PaymentRepository;
import com.example.back_AutoYa.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final Map<String, Payment> paymentIntents = new ConcurrentHashMap<>();

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final CompletionService completionService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // ==============================================================
    // 1️⃣ Crear un intent de pago temporal (no persistente)
    // ==============================================================
    public Map<String, Object> createPaymentIntent(Long reservationId, Double amount, String method) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + reservationId));

        String intentId = UUID.randomUUID().toString();

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setMethod(PaymentMethod.valueOf(method));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setDate(LocalDate.now());

        paymentIntents.put(intentId, payment);

        return Map.of(
                "intentId", intentId,
                "status", "CREATED",
                "reservationId", reservationId,
                "message", "Intent de pago creado exitosamente."
        );
    }

    // ==============================================================
    // 2️⃣ Capturar el intent y persistirlo solo una vez
    // ==============================================================
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

        // Cambiar estado y guardar
        payment.setStatus(PaymentStatus.COMPLETED);
        Payment saved = paymentRepository.save(payment);
        paymentIntents.remove(intentId); // eliminar de la memoria

        Reservation reservation = saved.getReservation();
        checkAndUpdateReservationStatus(reservation);

        return Map.of(
                "intentId", intentId,
                "status", "CAPTURED",
                "reservationId", reservation.getId(),
                "message", "Pago capturado y guardado correctamente."
        );
    }

    // ==============================================================
    // 3️⃣ Consultar estado del intent (sin guardar)
    // ==============================================================
    public Map<String, Object> getIntentStatus(String intentId) {
        Payment payment = paymentIntents.get(intentId);
        if (payment == null) {
            return Map.of("error", "Intent de pago no encontrado.");
        }

        return Map.of(
                "intentId", intentId,
                "status", payment.getStatus().name(),
                "reservationId", payment.getReservation().getId(),
                "message", "Estado obtenido correctamente."
        );
    }

    // ==============================================================
    // 4️⃣ Obtener todos los pagos (usando DTOs)
    // ==============================================================
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    // ==============================================================
    // 5️⃣ Crear pago directo (sin intent)
    // ==============================================================
    @Transactional
    public PaymentDTO createPayment(Long reservationId, Double amount, String method) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + reservationId));

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setMethod(PaymentMethod.valueOf(method));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setDate(LocalDate.now());

        Payment saved = paymentRepository.save(payment);
        completionService.completePayment(saved.getId());

        return convertToDTO(saved);
    }

    // ==============================================================
    // 6️⃣ Procesar pago de forma asíncrona
    // ==============================================================
    @Async
    @Transactional
    public void processPaymentAsync(Long paymentId) {
        scheduler.schedule(() -> {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);

            checkAndUpdateReservationStatus(payment.getReservation());
        }, 1, TimeUnit.MINUTES);
    }

    // ==============================================================
    // 🔸 Función auxiliar: actualizar estado de la reserva
    // ==============================================================
    private void checkAndUpdateReservationStatus(Reservation reservation) {
        double totalPaid = reservation.getPayments().stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Payment::getAmount)
                .sum();

        if (totalPaid >= reservation.getTotalPrice()) {
            reservation.setStatus(ReservationStatus.RESERVED);
            reservationRepository.save(reservation);
        }
    }

    // ==============================================================
    // 🔸 Conversor a DTO
    // ==============================================================
    private PaymentDTO convertToDTO(Payment payment) {
        return new PaymentDTO(
                payment.getId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getDate(),
                payment.getReservation().getId()
        );
    }
}
