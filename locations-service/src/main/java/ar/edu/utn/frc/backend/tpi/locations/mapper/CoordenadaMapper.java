package ar.edu.utn.frc.backend.tpi.locations.mapper;

import ar.edu.utn.frc.backend.tpi.locations.dto.CoordenadaDto;
import ar.edu.utn.frc.backend.tpi.locations.model.Coordenada;
import org.springframework.stereotype.Component;

@Component
public class CoordenadaMapper {

    public CoordenadaDto toDto(Coordenada coordenada) {
        if (coordenada == null) {
            return null;
        }
        return CoordenadaDto.builder()
                .id(coordenada.getId())
                .latitud(coordenada.getLatitud())
                .longitud(coordenada.getLongitud())
                .build();
    }

    public Coordenada toEntity(CoordenadaDto dto) {
        if (dto == null) {
            return null;
        }
        Coordenada coordenada = new Coordenada();
        coordenada.setId(dto.getId());
        coordenada.setLatitud(dto.getLatitud());
        coordenada.setLongitud(dto.getLongitud());
        return coordenada;
    }
}
