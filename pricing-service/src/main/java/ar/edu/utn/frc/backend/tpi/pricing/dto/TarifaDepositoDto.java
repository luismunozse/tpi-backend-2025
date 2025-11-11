package ar.edu.utn.frc.backend.tpi.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarifaDepositoDto {

    private Long id;
    private Double costoPorDia;
    private Double cargoPorIngreso;
    private Double cargoPorSalida;
}
