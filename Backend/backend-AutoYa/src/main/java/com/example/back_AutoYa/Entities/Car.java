package com.example.back_AutoYa.Entities;

import com.example.back_AutoYa.Entities.Enums.VehicleCategory;
import com.example.back_AutoYa.Entities.Enums.VehicleStatus;
import com.example.back_AutoYa.Entities.Enums.FuelType;
import com.example.back_AutoYa.Entities.Enums.TransmissionType;

public class Car {
    private Long id;
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

    // Constructors
    public Car() {}

    public Car(Long id, String brand, String model, int year, String color, VehicleCategory category,
               VehicleStatus status, FuelType fuelType, TransmissionType transmissionType,
               double pricePerDay, String licensePlate, String imageUrl) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.category = category;
        this.status = status;
        this.fuelType = fuelType;
        this.transmissionType = transmissionType;
        this.pricePerDay = pricePerDay;
        this.licensePlate = licensePlate;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public VehicleCategory getCategory() { return category; }
    public void setCategory(VehicleCategory category) { this.category = category; }

    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }

    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }

    public TransmissionType getTransmissionType() { return transmissionType; }
    public void setTransmissionType(TransmissionType transmissionType) { this.transmissionType = transmissionType; }

    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}