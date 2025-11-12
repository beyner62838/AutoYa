package com.example.back_AutoYa.Mapper;

import com.example.back_AutoYa.Entities.Payment;
import com.example.back_AutoYa.dto.PaymentDTO;

import java.util.List;

public class PaymentMapper {
    public static PaymentDTO toDTO(Payment payment) {
        if (payment == null) return null;

        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setDate(payment.getDate()); // 👈 String directo
        dto.setAmount(payment.getAmount());
        dto.setMethod(payment.getMethod());
        dto.setStatus(payment.getStatus());
        if (payment.getReservation() != null) {
            dto.setReservationId(payment.getReservation().getId());
        }
        return dto;
    }




    public static List<PaymentDTO> toDTOList(List<Payment> payments) {
        return payments.stream()
                .map(PaymentMapper::toDTO)
                .toList();
    }
}
