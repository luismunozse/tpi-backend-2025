package ar.edu.utn.frc.backend.tpi.fleet.mapper;

import ar.edu.utn.frc.backend.tpi.fleet.dto.CamionDto;
import ar.edu.utn.frc.backend.tpi.fleet.model.Camion;
import ar.edu.utn.frc.backend.tpi.fleet.model.EstadoCamion;
import ar.edu.utn.frc.backend.tpi.fleet.model.Transportista;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class CamionMapper {

    private final TransportistaMapper transportistaMapper;

    public CamionDto toDto(Camion camion) {
        if (camion == null) {
            return null;
        }
        return CamionDto.builder()
                .id(camion.getId())
                .dominio(camion.getDominio())
                .capacidadPesoKg(camion.getCapacidadPesoKg())
                .capacidadVolumenM3(camion.getCapacidadVolumenM3())
                .consumoCombustiblePorKm(camion.getConsumoCombustiblePorKm())
                .costoBasePorKm(camion.getCostoBasePorKm())
                .estado(camion.getEstado() != null ? camion.getEstado().name() : null)
                .transportista(transportistaMapper.toDto(camion.getTransportista()))
                .build();
    }

    public Camion toEntity(CamionDto dto) {
        if (dto == null) {
            return null;
        }
        Camion camion = new Camion();
        camion.setId(dto.getId());
        camion.setDominio(dto.getDominio());
        camion.setCapacidadPesoKg(dto.getCapacidadPesoKg());
        camion.setCapacidadVolumenM3(dto.getCapacidadVolumenM3());
        camion.setConsumoCombustiblePorKm(dto.getConsumoCombustiblePorKm());
        camion.setCostoBasePorKm(dto.getCostoBasePorKm());
        camion.setEstado(mapearEstado(dto.getEstado()));
        camion.setTransportista(transportistaMapper.toEntity(dto.getTransportista()));
        return camion;
    }

    private EstadoCamion mapearEstado(String estado) {
        if (estado == null) {
            return null;
        }
        return EstadoCamion.valueOf(estado.toUpperCase(Locale.ROOT));
    }

    public Transportista referenciaTransportista(Long id) {
        if (id == null) {
            return null;
        }
        Transportista transportista = new Transportista();
        transportista.setId(id);
        return transportista;
    }
}
