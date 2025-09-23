package com.example.back_AutoYa.repository;

import com.example.back_AutoYa.Entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

}
