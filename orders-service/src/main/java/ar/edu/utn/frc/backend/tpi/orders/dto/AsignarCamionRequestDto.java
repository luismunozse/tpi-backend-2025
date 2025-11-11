package ar.edu.utn.frc.backend.tpi.orders.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarCamionRequestDto {

    @NotNull
    private Long camionId;
}
