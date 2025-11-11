package ar.edu.utn.frc.backend.tpi.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculoTarifaRequestDto {

    private Double distanciaTotalKm;
    private Double distanciaRecorridaPorCamionKm;
    private Double pesoContenedorKg;
    private Double volumenContenedorM3;
    private Double consumoCamionLtsPorKm;
    private Double costoCombustiblePorLitro;
    private Integer cantidadTramos;
    private Integer diasTotalesEnDeposito;
    private List<Long> recargosAplicados;
}
