package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.Enums.PaymentMethod;
import com.example.back_AutoYa.Entities.Enums.PaymentStatus;
import com.example.back_AutoYa.Entities.Enums.ReservationStatus;
import com.example.back_AutoYa.Entities.Payment;
import com.example.back_AutoYa.Entities.Reservation;
import com.example.back_AutoYa.Entities.User;
import com.example.back_AutoYa.Mapper.PaymentMapper;
import com.example.back_AutoYa.Mapper.ReservationMapper;
import com.example.back_AutoYa.dto.PaymentDTO;
import com.example.back_AutoYa.dto.ReservationDTO;
import com.example.back_AutoYa.repository.PaymentRepository;
import com.example.back_AutoYa.repository.ReservationRepository;
import com.example.back_AutoYa.repository.UserRepository;
import com.example.back_AutoYa.utils.ReceiptGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    // 🧠 Simulación de intents de pago (memoria temporal)
    private final Map<String, Payment> paymentIntents = new ConcurrentHashMap<>();

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final CompletionService completionService;
    private final ReservationService reservationService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // ==============================================================
    // 🔹 1️⃣ Crear un intent de pago temporal (no persistente)
    // ==============================================================
    public Map<String, Object> createPaymentIntent(Long reservationId, Double amount, String method) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + reservationId));

        String intentId = UUID.randomUUID().toString();

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setMethod(PaymentMethod.valueOf(method));
        payment.setStatus(PaymentStatus.PENDING); // Aún no confirmado
        payment.setDate(LocalDate.now());

        // Guardamos el intent en memoria
        paymentIntents.put(intentId, payment);

        return Map.of(
                "intentId", intentId,
                "status", "CREATED",
                "reservationId", reservationId,
                "message", "Intent de pago creado y asociado a la reserva."
        );
    }

    // ==============================================================
    // 🔹 2️⃣ Capturar un intent → guardar en BD
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

        // ✅ Cambiar estado a COMPLETED y guardar en BD
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        // Verificar si la reserva queda completamente pagada
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

    // ==============================================================
    // 🔹 3️⃣ Consultar el estado actual de un intent
    //    (👉 usado en el endpoint /confirm)
    // ==============================================================
    public Map<String, Object> getIntentStatus(String intentId) {
        if (intentId == null || !paymentIntents.containsKey(intentId)) {
            return Map.of("error", "Intent de pago no encontrado.");
        }

        Payment payment = paymentIntents.get(intentId);

        return Map.of(
                "intentId", intentId,
                "status", payment.getStatus().name(), // Ej: PENDING, COMPLETED
                "reservationId", payment.getReservation().getId(),
                "message", "Estado del intent obtenido correctamente."
        );
    }

    // ==============================================================
    // 🔹 4️⃣ Obtener todos los pagos (persistidos)
    // ==============================================================
    public List<PaymentDTO> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return PaymentMapper.toDTOList(payments);
    }

    public List<PaymentDTO> getAllPaymentsById(Long userId) {
        List<ReservationDTO> reservations = reservationService.getAllReservationsById(userId);
        List<Long> reservationIds = reservations.stream()
                .map(ReservationDTO::getId)
                .collect(Collectors.toList());

        List<Payment> payments = paymentRepository.findByReservationIdIn(reservationIds);
        return PaymentMapper.toDTOList(payments);
    }



    // ==============================================================
    // 🔹 5️⃣ Crear un pago real (se usa en /confirm)
    // ==============================================================
    @Transactional
    public Payment createPayment(Long reservationId, Double amount, String method) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + reservationId));

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setMethod(PaymentMethod.valueOf(method));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setDate(LocalDate.now());


        paymentRepository.save(payment);

        // Notificar al servicio de completado
        completionService.completePayment(payment.getId());

        return payment;
    }

    // ==============================================================
    // 🔹 6️⃣ Procesar pago de forma asíncrona (simulación)
    // ==============================================================
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

    // ==============================================================
    // 🔹 7️⃣ Guardar pago directamente (para uso desde controller)
    // ==============================================================
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

}