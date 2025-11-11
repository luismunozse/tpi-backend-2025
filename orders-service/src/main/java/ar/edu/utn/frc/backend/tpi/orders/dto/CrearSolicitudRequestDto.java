package ar.edu.utn.frc.backend.tpi.orders.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearSolicitudRequestDto {

    @NotBlank
    private String identificadorContenedor;

    @NotNull
    @Min(1)
    private Double pesoContenedorKg;

    @NotNull
    @Min(1)
    private Double volumenContenedorM3;

    @NotBlank
    private String nombreCliente;

    @NotBlank
    private String emailCliente;

    @NotBlank
    private String telefonoCliente;

    @NotNull
    private List<CrearSolicitudRequestDto.TramoRequest> tramos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TramoRequest {
        @NotNull
        private Integer orden;
        @NotBlank
        private String tipo;
        @NotBlank
        private String origenNombre;
        @NotBlank
        private String destinoNombre;
        private Double origenLatitud;
        private Double origenLongitud;
        private Double destinoLatitud;
        private Double destinoLongitud;
        @NotNull
        @Min(0)
        private Double distanciaEstimadaKm;
        @NotNull
        @Min(0)
        private Double duracionEstimadaHoras;
    }
}
