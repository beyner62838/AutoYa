package com.example.back_AutoYa.Entities.Enums;

public enum ReservationStatus {
    RESERVED,
    CANCELLED,
    COMPLETED,
    IN_PROGRESS,
    BLOCKED, // <-- Agregado para bloqueos de propietario
    ON_HOLD // hold temporal de cliente (10 minutos)
    BLOCKED // <-- Agregado para bloqueos de propietario
}