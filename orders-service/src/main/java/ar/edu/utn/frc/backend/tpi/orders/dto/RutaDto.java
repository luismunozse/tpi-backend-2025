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
public class RutaDto {

    private Long id;
    private Double distanciaTotalKm;
    private Double duracionTotalHoras;
    private List<TramoDto> tramos;
}
