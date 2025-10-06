package com.example.back_AutoYa.dto;

import com.example.back_AutoYa.Entities.Enums.PaymentMethod;
import com.example.back_AutoYa.Entities.Enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDate date;
    private Long reservationId;
}
