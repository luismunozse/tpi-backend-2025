package ar.edu.utn.frc.backend.tpi.orders.mapper;

import ar.edu.utn.frc.backend.tpi.orders.dto.RutaDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.TramoDto;
import ar.edu.utn.frc.backend.tpi.orders.model.Ruta;
import ar.edu.utn.frc.backend.tpi.orders.model.Tramo;
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
        List<TramoDto> tramos = ruta.getTramos() == null ? List.of()
                : ruta.getTramos().stream().map(tramoMapper::toDto).collect(Collectors.toList());
        return RutaDto.builder()
                .id(ruta.getId())
                .distanciaTotalKm(ruta.getDistanciaTotalKm())
                .duracionTotalHoras(ruta.getDuracionTotalHoras())
                .tramos(tramos)
                .build();
    }

    public Ruta toEntity(RutaDto dto) {
        if (dto == null) {
            return null;
        }
        Ruta ruta = new Ruta();
        ruta.setId(dto.getId());
        ruta.setDistanciaTotalKm(dto.getDistanciaTotalKm());
        ruta.setDuracionTotalHoras(dto.getDuracionTotalHoras());
        List<Tramo> tramos = dto.getTramos() == null ? new ArrayList<>()
                : dto.getTramos().stream().map(tramoMapper::toEntity).collect(Collectors.toCollection(ArrayList::new));
        tramos.forEach(tramo -> tramo.setRuta(ruta));
        ruta.setTramos(tramos);
        return ruta;
    }
}
