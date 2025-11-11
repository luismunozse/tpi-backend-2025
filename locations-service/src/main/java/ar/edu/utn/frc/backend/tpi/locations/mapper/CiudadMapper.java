package ar.edu.utn.frc.backend.tpi.locations.mapper;

import ar.edu.utn.frc.backend.tpi.locations.dto.CiudadDto;
import ar.edu.utn.frc.backend.tpi.locations.model.Ciudad;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CiudadMapper {

    private final ProvinciaMapper provinciaMapper;
    private final CoordenadaMapper coordenadaMapper;

    public CiudadDto toDto(Ciudad ciudad) {
        if (ciudad == null) {
            return null;
        }
        return CiudadDto.builder()
                .id(ciudad.getId())
                .nombre(ciudad.getNombre())
                .provincia(provinciaMapper.toDto(ciudad.getProvincia()))
                .coordenada(coordenadaMapper.toDto(ciudad.getCoordenada()))
                .build();
    }

    public Ciudad toEntity(CiudadDto dto) {
        if (dto == null) {
            return null;
        }
        Ciudad ciudad = new Ciudad();
        ciudad.setId(dto.getId());
        ciudad.setNombre(dto.getNombre());
        ciudad.setProvincia(provinciaMapper.toEntity(dto.getProvincia()));
        ciudad.setCoordenada(coordenadaMapper.toEntity(dto.getCoordenada()));
        return ciudad;
    }
}
