package ar.edu.utn.frc.backend.tpi.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarifaBaseDto {

    private Long id;
    private String tipoCamion;
    private Double rangoPesoMinKg;
    private Double rangoPesoMaxKg;
    private Double rangoVolumenMinM3;
    private Double rangoVolumenMaxM3;
    private Double costoPorKilometro;
    private Double costoPorKilometroCombustible;
    private Double costoFijoGestion;
}
