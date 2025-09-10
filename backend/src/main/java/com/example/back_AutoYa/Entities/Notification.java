package com.example.back_AutoYa.Entities;

import com.example.back_AutoYa.Entities.Enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // <- Anotación crítica
@Table(name = "notification")
public class Notification {

    @Id // <- Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false, length = 500) // length para limitar longitud
    private String message;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private boolean read;
}