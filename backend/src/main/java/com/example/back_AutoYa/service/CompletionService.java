package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.Car;
import com.example.back_AutoYa.Entities.Enums.PaymentStatus;
import com.example.back_AutoYa.Entities.Enums.ReservationStatus;
import com.example.back_AutoYa.Entities.Payment;
import com.example.back_AutoYa.Entities.Reservation;
import com.example.back_AutoYa.repository.CarRepository;
import com.example.back_AutoYa.repository.PaymentRepository;
import com.example.back_AutoYa.repository.ReservationRepository;
import com.example.back_AutoYa.utils.ReceiptGenerator;
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
    private final EmailService emailService;

    @Async
    @Transactional
    public void completePayment(Long paymentId) {
        // Ejecutar asincrónicamente después de 1 minuto
        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS)
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
                    String html = ReceiptGenerator.generateHtml(reservation);
                    // guardar archivo html localmente para pruebas
                    try {
                        java.nio.file.Path receiptsDir = java.nio.file.Paths.get("/tmp/autoya/receipts");
                        java.nio.file.Files.createDirectories(receiptsDir);
                        java.nio.file.Files.writeString(receiptsDir.resolve("receipt-" + reservation.getId() + ".html"), html);
                    } catch (Exception ex) {
                        // no interrumpir si falla el guardado
                        System.out.println("No se pudo guardar HTML: " + ex.getMessage());
                    }
                    if (reservation.getClient() != null && reservation.getClient().getEmail() != null && !reservation.getClient().getEmail().isEmpty()) {
                        try {
                            emailService.sendHtmlEmail(reservation.getClient().getEmail(), "Recibo AutoYa - Reserva " + reservation.getId(), html);
                        } catch (Exception me) {
                            System.out.println("No se pudo enviar email: " + me.getMessage());
                        }
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
