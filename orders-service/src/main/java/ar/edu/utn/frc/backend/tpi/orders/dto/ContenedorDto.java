package ar.edu.utn.frc.backend.tpi.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorDto {

    private Long id;
    private String identificador;
    private Double pesoKg;
    private Double volumenM3;
    private String estado;
    private ClienteDto cliente;
}
