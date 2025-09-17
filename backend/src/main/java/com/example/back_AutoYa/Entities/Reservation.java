package com.example.back_AutoYa.Entities;

import com.example.back_AutoYa.Entities.Enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relación con User (cliente que hace la reserva) ---
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- Relación con Car (vehículo reservado) ---
    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(name = "start_date", nullable = false)
    private String startDate;

    @Column(name = "end_date", nullable = false)
    private String endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    // --- Relación con Payment (pagos de esta reserva) ---
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;
}
