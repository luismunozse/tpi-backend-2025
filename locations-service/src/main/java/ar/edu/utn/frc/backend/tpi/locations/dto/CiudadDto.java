package ar.edu.utn.frc.backend.tpi.locations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CiudadDto {

    private Integer id;
    private String nombre;
    private ProvinciaDto provincia;
    private CoordenadaDto coordenada;
}
