package com.example.back_AutoYa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalPrice;
    private String status;

    private CarDTO car;
    private UserDTO client;
    private UserDTO owner;
}

