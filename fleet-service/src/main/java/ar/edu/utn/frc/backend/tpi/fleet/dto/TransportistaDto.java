package ar.edu.utn.frc.backend.tpi.fleet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportistaDto {

    private Long id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
}
