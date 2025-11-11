package ar.edu.utn.frc.backend.tpi.locations.dto;

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
    private String nombre;
    private String descripcion;
    private List<TramoDto> tramos;
}
