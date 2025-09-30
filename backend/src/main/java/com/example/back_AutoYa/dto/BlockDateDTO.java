package com.example.back_AutoYa.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BlockDateDTO {
    private Long carId;
    private LocalDate date;
    private Double price;
}