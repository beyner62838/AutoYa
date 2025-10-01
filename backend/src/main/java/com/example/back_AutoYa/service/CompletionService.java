package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.Car;
import com.example.back_AutoYa.Entities.Enums.PaymentStatus;
import com.example.back_AutoYa.Entities.Enums.ReservationStatus;
import com.example.back_AutoYa.Entities.Payment;
import com.example.back_AutoYa.Entities.Reservation;
import com.example.back_AutoYa.repository.CarRepository;
import com.example.back_AutoYa.repository.PaymentRepository;
import com.example.back_AutoYa.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CompletionService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;

    @Async
    @Transactional
    public void completePayment(Long paymentId) {
        // Ejecutar asincrónicamente después de 3 minutos
        CompletableFuture.delayedExecutor(1, TimeUnit.MINUTES)
                .execute(() -> {
                    Payment payment = paymentRepository.findById(paymentId)
                            .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

                    // Cambiar estado del pago
                    payment.setStatus(PaymentStatus.COMPLETED);
                    paymentRepository.save(payment);

                    // Obtener reserva asociada
                    Reservation reservation = payment.getReservation();

                    // Calcular pagos COMPLETED
                    double totalPaid = reservation.getPayments().stream()
                            .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                            .mapToDouble(Payment::getAmount)
                            .sum();

                    // Actualizar estado de la reserva si corresponde
                    if (totalPaid >= reservation.getTotalPrice()) {
                        reservation.setStatus(ReservationStatus.RESERVED);
                        reservationRepository.save(reservation);
                    }
                });
    }


    @Async
    @Transactional
    public void deleteHoldReservation(Long reservationId) {
        CompletableFuture.delayedExecutor(10, TimeUnit.MINUTES)
                .execute(() -> {
                    Reservation reservation = reservationRepository.findById(reservationId)
                            .orElseThrow(() -> new RuntimeException("Reservation not found"));
                    reservationRepository.delete(reservation);
                });
    }
}
