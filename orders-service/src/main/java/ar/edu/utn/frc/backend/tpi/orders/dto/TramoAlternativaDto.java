package ar.edu.utn.frc.backend.tpi.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TramoAlternativaDto {

    private Integer orden;
    private String tipo;
    private String origenNombre;
    private String destinoNombre;
    private Double origenLatitud;
    private Double origenLongitud;
    private Double destinoLatitud;
    private Double destinoLongitud;
    private Double distanciaKm;
    private Double duracionHoras;
    private Long depositoId;
}
