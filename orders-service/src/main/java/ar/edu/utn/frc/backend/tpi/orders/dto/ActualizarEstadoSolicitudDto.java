package ar.edu.utn.frc.backend.tpi.orders.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoSolicitudDto {

    @NotBlank
    private String estado;
}
