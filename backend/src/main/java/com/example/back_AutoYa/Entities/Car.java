package com.example.back_AutoYa.Entities;

import com.example.back_AutoYa.Entities.Enums.VehicleCategory;
import com.example.back_AutoYa.Entities.Enums.VehicleStatus;
import com.example.back_AutoYa.Entities.Enums.FuelType;
import com.example.back_AutoYa.Entities.Enums.TransmissionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // <- Anotación crítica
@Table(name = "car")
public class Car {

    @Id // <- Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transmission_type", nullable = false)
    private TransmissionType transmissionType;

    @Column(name = "price_per_day", nullable = false)
    private double pricePerDay;

    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @Column(name = "image_url")
    private String imageUrl;
}