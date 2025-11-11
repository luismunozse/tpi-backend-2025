package ar.edu.utn.frc.backend.tpi.orders.mapper;

import ar.edu.utn.frc.backend.tpi.orders.dto.ContenedorDto;
import ar.edu.utn.frc.backend.tpi.orders.model.Contenedor;
import ar.edu.utn.frc.backend.tpi.orders.model.EstadoContenedor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ContenedorMapper {

    private final ClienteMapper clienteMapper;

    public ContenedorDto toDto(Contenedor contenedor) {
        if (contenedor == null) {
            return null;
        }
        return ContenedorDto.builder()
                .id(contenedor.getId())
                .identificador(contenedor.getIdentificador())
                .pesoKg(contenedor.getPesoKg())
                .volumenM3(contenedor.getVolumenM3())
                .estado(contenedor.getEstado() != null ? contenedor.getEstado().name() : null)
                .cliente(clienteMapper.toDto(contenedor.getCliente()))
                .build();
    }

    public Contenedor toEntity(ContenedorDto dto) {
        if (dto == null) {
            return null;
        }
        Contenedor contenedor = new Contenedor();
        contenedor.setId(dto.getId());
        contenedor.setIdentificador(dto.getIdentificador());
        contenedor.setPesoKg(dto.getPesoKg());
        contenedor.setVolumenM3(dto.getVolumenM3());
        contenedor.setEstado(parseEstado(dto.getEstado()));
        contenedor.setCliente(clienteMapper.toEntity(dto.getCliente()));
        return contenedor;
    }

    private EstadoContenedor parseEstado(String estado) {
        if (estado == null) {
            return null;
        }
        return EstadoContenedor.valueOf(estado.toUpperCase(Locale.ROOT));
    }
}
