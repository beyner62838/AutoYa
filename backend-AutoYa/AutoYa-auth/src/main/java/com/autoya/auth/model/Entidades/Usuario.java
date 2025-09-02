package com.autoya.auth.model.Entidades;

import com.autoya.auth.model.Entidades.Enums.RolUsuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;

    @Column(name = "nombres", length = 100, nullable = false)
    private String nombres;

    @Column(name = "apellidos", length = 80, nullable = false)
    private String apellidos;

    @Column(name = "telefono", length = 15, nullable = false)
    private String telefono;

    @Column(name = "email", length = 80, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", length = 15)
    private RolUsuario rol;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "foto_perfil_url")
    private String fotoPerfilUrl;

    @Builder.Default
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Carro> carros = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> pagos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notificacion> notificaciones = new ArrayList<>();

    // Métodos de conveniencia
    public void addCarro(Carro carro) {
        carros.add(carro);
        carro.setUsuario(this);
    }

    public void removeCarro(Carro carro) {
        carros.remove(carro);
        carro.setUsuario(null);
    }

    public void addReserva(Reserva reserva) {
        reservas.add(reserva);
        reserva.setUsuario(this);
    }

    public void addNotificacion(Notificacion notificacion) {
        notificaciones.add(notificacion);
        notificacion.setUsuario(this);
    }
}