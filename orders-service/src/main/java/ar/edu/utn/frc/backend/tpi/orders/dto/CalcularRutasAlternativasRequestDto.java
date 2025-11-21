package ar.edu.utn.frc.backend.tpi.orders.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalcularRutasAlternativasRequestDto {

    @NotNull
    private Double latitudOrigen;

    @NotNull
    private Double longitudOrigen;

    @NotNull
    private Double latitudDestino;

    @NotNull
    private Double longitudDestino;

    @NotNull
    @Min(1)
    private Double pesoContenedorKg;

    @NotNull
    @Min(1)
    private Double volumenContenedorM3;

    private String nombreOrigen;
    private String nombreDestino;
}
