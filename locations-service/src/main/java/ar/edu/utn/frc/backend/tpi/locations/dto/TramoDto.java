package ar.edu.utn.frc.backend.tpi.locations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TramoDto {

    private Long id;
    private Integer orden;
    private String tipo;
    private Double distanciaEstimadaKm;
    private CoordenadaDto origen;
    private CoordenadaDto destino;
    private Integer origenDepositoId;
    private Integer destinoDepositoId;
}
