package com.example.back_AutoYa.Entities;

import com.example.back_AutoYa.Entities.Enums.ReservationStatus;

public class Reservation {
    private Long id;
    private Long userId;
    private Long carId;
    private String startDate;
    private String endDate;
    private ReservationStatus status;
    private double totalPrice;

    // Constructors
    public Reservation() {}

    public Reservation(Long id, Long userId, Long carId, String startDate, String endDate, ReservationStatus status, double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.carId = carId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}