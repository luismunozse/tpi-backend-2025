package ar.edu.utn.frc.backend.tpi.fleet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CamionDto {

    private Long id;
    private String dominio;
    private Double capacidadPesoKg;
    private Double capacidadVolumenM3;
    private Double consumoCombustiblePorKm;
    private Double costoBasePorKm;
    private String estado;
    private TransportistaDto transportista;
}
