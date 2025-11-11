package ar.edu.utn.frc.backend.tpi.locations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositoDto {

    private Integer id;
    private String nombre;
    private String direccion;
    private Integer altura;
    private CiudadDto ciudad;
    private ProvinciaDto provincia;
    private CoordenadaDto coordenada;
}
