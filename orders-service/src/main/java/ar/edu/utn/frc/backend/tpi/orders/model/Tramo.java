package ar.edu.utn.frc.backend.tpi.orders.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tramos")
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer orden;

    @Enumerated(EnumType.STRING)
    private TipoTramo tipo;

    private String origenNombre;
    private String destinoNombre;

    private Double origenLatitud;
    private Double origenLongitud;
    private Double destinoLatitud;
    private Double destinoLongitud;

    private Double distanciaEstimadaKm;
    private Double duracionEstimadaHoras;

    @Enumerated(EnumType.STRING)
    private EstadoTramo estado;

    private LocalDateTime fechaEstimadaInicio;
    private LocalDateTime fechaEstimadaFin;
    private LocalDateTime fechaRealInicio;
    private LocalDateTime fechaRealFin;

    private Double costoEstimado;
    private Double costoReal;

    @Column(name = "camion_id")
    private Long camionAsignadoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id")
    private Ruta ruta;
}
