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
public class CalculoTarifaResponseDto {

    private Double costoBaseKm;
    private Double costoCombustible;
    private Double costosDeposito;
    private Double costosGestion;
    private Double recargos;
    private Double costoTotal;

    private List<DetalleRecargoDto> detalleRecargos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleRecargoDto {
        private Long id;
        private String descripcion;
        private Double porcentaje;
        private Double montoFijo;
        private Double montoAplicado;
    }
}
