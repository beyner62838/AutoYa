package com.example.back_AutoYa.Entities;

import com.example.back_AutoYa.Entities.Enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // <- Anotación crítica para que JPA la reconozca
@Table(name = "reservation") // Nombre de la tabla en la BD
public class Reservation {

    @Id // <- Anotación crítica para la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "car_id", nullable = false)
    private Long carId;

    @Column(name = "start_date", nullable = false)
    private String startDate;

    @Column(name = "end_date", nullable = false)
    private String endDate;

    @Enumerated(EnumType.STRING) // Para almacenar el enum como String
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;
}