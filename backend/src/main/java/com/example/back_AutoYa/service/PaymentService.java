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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PaymentService {
    // Simulación de intents de pago
    private final Map<String, String> paymentIntents = new ConcurrentHashMap<>();

    public Map<String, Object> createPaymentIntent() {
        String intentId = UUID.randomUUID().toString();
        paymentIntents.put(intentId, "CREATED");
        return Map.of(
                "intentId", intentId,
                "status", "CREATED"
        );
    }

    public Map<String, Object> capturePayment(String intentId) {
        if (intentId != null && paymentIntents.containsKey(intentId)) {
            paymentIntents.put(intentId, "CAPTURED");
            return Map.of(
                    "intentId", intentId,
                    "status", "CAPTURED",
                    "message", "Pago simulado capturado correctamente."
            );
        } else {
            return Map.of(
                    "error", "Intent de pago no encontrado."
            );
        }
    }

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final CompletionService completionService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public List<Payment> getAllPayments(){
        return paymentRepository.findAll();
    }

    @Transactional
    public Payment createPayment(Long reservationId, Double amount, String method){


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
        // Aquí podrías programar el cambio de estado con scheduler o temporizador no bloqueante
        // Ejemplo con ScheduledExecutorService sin bloquear el hilo HTTP:
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
