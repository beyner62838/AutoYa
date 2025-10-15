package com.example.back_AutoYa.repository;

import com.example.back_AutoYa.Entities.Payment;
import com.example.back_AutoYa.dto.PaymentDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findByReservationIdIn(List<Long> reservationIds);
}
