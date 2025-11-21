package ar.edu.utn.frc.backend.tpi.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutaAlternativaDto {

    private Integer id;
    private String descripcion;
    private List<TramoAlternativaDto> tramos;
    private Double distanciaTotalKm;
    private Double tiempoEstimadoHoras;
    private Double costoEstimado;
    private Boolean recomendada;
    private Integer cantidadDepositos;
}
