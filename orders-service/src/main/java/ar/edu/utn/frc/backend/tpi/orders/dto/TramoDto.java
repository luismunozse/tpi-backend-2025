package ar.edu.utn.frc.backend.tpi.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TramoDto {

    private Long id;
    private Integer orden;
    private String tipo;
    private String estado;
    private String origenNombre;
    private String destinoNombre;
    private Double origenLatitud;
    private Double origenLongitud;
    private Double destinoLatitud;
    private Double destinoLongitud;
    private Double distanciaEstimadaKm;
    private Double duracionEstimadaHoras;
    private Double costoEstimado;
    private Double costoReal;
    private Long camionAsignadoId;
    private LocalDateTime fechaEstimadaInicio;
    private LocalDateTime fechaEstimadaFin;
    private LocalDateTime fechaRealInicio;
    private LocalDateTime fechaRealFin;
}
