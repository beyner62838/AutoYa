package com.example.back_AutoYa.dto;

import com.example.back_AutoYa.Entities.Enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarDTO {
    private Long id;
    private Long ownerId; // Solo el ID del propietario
    private String brand;
    private String model;
    private int year;
    private String color;
    private String city;
    private VehicleCategory category;
    private VehicleStatus status;
    private FuelType fuelType;
    private TransmissionType transmissionType;
    private double pricePerDay;
    private String licensePlate;
}