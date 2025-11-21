package ar.edu.utn.frc.backend.tpi.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutasAlternativasResponseDto {

    private String origen;
    private String destino;
    private List<RutaAlternativaDto> alternativas;
    private Integer totalAlternativas;
}
