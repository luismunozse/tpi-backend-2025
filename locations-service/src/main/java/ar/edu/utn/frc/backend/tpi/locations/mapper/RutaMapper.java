package ar.edu.utn.frc.backend.tpi.locations.mapper;

import ar.edu.utn.frc.backend.tpi.locations.dto.RutaDto;
import ar.edu.utn.frc.backend.tpi.locations.dto.TramoDto;
import ar.edu.utn.frc.backend.tpi.locations.model.Ruta;
import ar.edu.utn.frc.backend.tpi.locations.model.Tramo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RutaMapper {

    private final TramoMapper tramoMapper;

    public RutaDto toDto(Ruta ruta) {
        if (ruta == null) {
            return null;
        }
        List<TramoDto> tramos = ruta.getTramos() == null
                ? List.of()
                : ruta.getTramos().stream()
                .map(tramoMapper::toDto)
                .collect(Collectors.toList());

        return RutaDto.builder()
                .id(ruta.getId())
                .nombre(ruta.getNombre())
                .descripcion(ruta.getDescripcion())
                .tramos(tramos)
                .build();
    }

    public Ruta toEntity(RutaDto dto) {
        if (dto == null) {
            return null;
        }
        Ruta ruta = new Ruta();
        ruta.setId(dto.getId());
        ruta.setNombre(dto.getNombre());
        ruta.setDescripcion(dto.getDescripcion());

        List<Tramo> tramos = dto.getTramos() == null
                ? new ArrayList<>()
                : dto.getTramos().stream()
                    .map(tramoMapper::toEntity)
                    .collect(Collectors.toCollection(ArrayList::new));

        tramos.forEach(tramo -> tramo.setRuta(ruta));
        ruta.setTramos(tramos);
        return ruta;
    }
}
