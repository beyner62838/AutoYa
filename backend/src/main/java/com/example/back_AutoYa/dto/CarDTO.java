package com.example.back_AutoYa.dto;

import com.example.back_AutoYa.Entities.Enums.*;
import lombok.Data;

@Data
public class CarDTO {
    private Long id;
    private Long ownerId; // Solo el ID del propietario
    private String brand;
    private String model;
    private int year;
    private String color;
    private VehicleCategory category;
    private VehicleStatus status;
    private FuelType fuelType;
    private TransmissionType transmissionType;
    private double pricePerDay;
    private String licensePlate;
    private String imageUrl;
}