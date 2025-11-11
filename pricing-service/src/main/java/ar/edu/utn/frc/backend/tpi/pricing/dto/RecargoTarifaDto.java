package ar.edu.utn.frc.backend.tpi.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecargoTarifaDto {

    private Long id;
    private String descripcion;
    private Double porcentaje;
    private Double montoFijo;
}
