package com.example.back_AutoYa.Entidades;

import com.example.back_AutoYa.Entidades.Enums.EstadoPago;
import com.example.back_AutoYa.Entidades.Enums.MetodoPago;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pago_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "monto", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo")
    private MetodoPago metodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoPago estado;

    @Column(name = "referencia_pago", length = 50)
    private String referenciaPago;

    @Column(name = "fecha_pago")
    private java.time.LocalDateTime fechaPago;
}