package com.autoya.auth.model.Entidades;


import com.autoya.auth.model.Entidades.Enums.CategoriaVehiculo;
import com.autoya.auth.model.Entidades.Enums.EstadoVehiculo;
import com.autoya.auth.model.Entidades.Enums.TipoCombustible;
import com.autoya.auth.model.Entidades.Enums.TipoTransmision;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carro_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "placa", length = 7, nullable = false, unique = true)
    private String placa;

    @Column(name = "marca", length = 20)
    private String marca;

    @Column(name = "modelo", length = 4)
    private String modelo;

    @Column(name = "color", length = 30)
    private String color;

    // FORMA CORRECTA:
    @Column(name = "precio_dia", columnDefinition = "NUMERIC(5,1)")
    private Double precioDia;

    @Column(name = "descripcion_adicional", columnDefinition = "TEXT")
    private String descripcionAdicional;

    @Column(name = "imagen_principal_url", columnDefinition = "TEXT")
    private String imagenPrincipalUrl;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "carro_imagenes", joinColumns = @JoinColumn(name = "carro_id"))
    @Column(name = "imagen_url")
    private List<String> imagenesUrls = new ArrayList<>();

    @Column(name = "kilometraje", length = 10)
    private String kilometraje;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_vehiculo")
    private EstadoVehiculo estadoVehiculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_combustible")
    private TipoCombustible tipoCombustible;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transmision")
    private TipoTransmision tipoTransmision;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private CategoriaVehiculo categoria;

    @Column(name = "numero_puertas")
    private Integer numeroPuertas;

    @Column(name = "numero_asientos")
    private Integer numeroAsientos;

    @Column(name = "ciudad", length = 50)
    private String ciudad;

    @Builder.Default
    @OneToMany(mappedBy = "carro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    // Métodos de conveniencia
    public void addImagenUrl(String imagenUrl) {
        this.imagenesUrls.add(imagenUrl);
    }

    public void removeImagenUrl(String imagenUrl) {
        this.imagenesUrls.remove(imagenUrl);
    }

    public void addReserva(Reserva reserva) {
        reservas.add(reserva);
        reserva.setCarro(this);
    }
}