package ar.edu.utn.frc.backend.tpi.locations.mapper;

import ar.edu.utn.frc.backend.tpi.locations.dto.ProvinciaDto;
import ar.edu.utn.frc.backend.tpi.locations.model.Provincia;
import org.springframework.stereotype.Component;

@Component
public class ProvinciaMapper {

    public ProvinciaDto toDto(Provincia provincia) {
        if (provincia == null) {
            return null;
        }
        return ProvinciaDto.builder()
                .id(provincia.getId())
                .nombre(provincia.getNombre())
                .build();
    }

    public Provincia toEntity(ProvinciaDto dto) {
        if (dto == null) {
            return null;
        }
        Provincia provincia = new Provincia();
        provincia.setId(dto.getId());
        provincia.setNombre(dto.getNombre());
        return provincia;
    }
}
