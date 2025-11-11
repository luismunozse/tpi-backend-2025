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
public class SolicitudTransporteDto {

    private Long id;
    private String numeroSolicitud;
    private String estado;
    private ClienteDto cliente;
    private ContenedorDto contenedor;
    private RutaDto ruta;
    private Double costoEstimado;
    private Double costoFinal;
    private Double tiempoEstimadoHoras;
    private Double tiempoRealHoras;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
