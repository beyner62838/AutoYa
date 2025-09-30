package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.Car;
import com.example.back_AutoYa.Entities.Enums.PaymentMethod;
import com.example.back_AutoYa.Entities.Enums.PaymentStatus;
import com.example.back_AutoYa.Entities.Enums.ReservationStatus;
import com.example.back_AutoYa.Entities.Payment;
import com.example.back_AutoYa.Entities.Reservation;
import com.example.back_AutoYa.repository.CarRepository;
import com.example.back_AutoYa.repository.PaymentRepository;
import com.example.back_AutoYa.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final CompletionService completionService;
    private final CarRepository carRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public List<Payment> getAllPayments(){
        return paymentRepository.findAll();
    }

    @Transactional
    public Long createHoldReservation(Long carId){
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.ON_HOLD);
        reservation.setCar(car);
        reservationRepository.save(reservation);
        return reservation.getId();
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
}
