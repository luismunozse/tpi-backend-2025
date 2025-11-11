package ar.edu.utn.frc.backend.tpi.fleet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MantenimientoDto {

    private Long id;
    private Long camionId;
    private String descripcion;
    private LocalDate fechaProgramada;
    private LocalDate fechaRealizada;
    private Boolean completado;
}
