package com.example.back_AutoYa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityDTO {
    private LocalDate startDate;
    private LocalDate endDate;
}
