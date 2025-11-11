package ar.edu.utn.frc.backend.tpi.fleet.mapper;

import ar.edu.utn.frc.backend.tpi.fleet.dto.MantenimientoDto;
import ar.edu.utn.frc.backend.tpi.fleet.model.Camion;
import ar.edu.utn.frc.backend.tpi.fleet.model.Mantenimiento;
import org.springframework.stereotype.Component;

@Component
public class MantenimientoMapper {

    public MantenimientoDto toDto(Mantenimiento mantenimiento) {
        if (mantenimiento == null) {
            return null;
        }
        return MantenimientoDto.builder()
                .id(mantenimiento.getId())
                .camionId(mantenimiento.getCamion() != null ? mantenimiento.getCamion().getId() : null)
                .descripcion(mantenimiento.getDescripcion())
                .fechaProgramada(mantenimiento.getFechaProgramada())
                .fechaRealizada(mantenimiento.getFechaRealizada())
                .completado(mantenimiento.isCompletado())
                .build();
    }

    public Mantenimiento toEntity(MantenimientoDto dto) {
        if (dto == null) {
            return null;
        }
        Mantenimiento mantenimiento = new Mantenimiento();
        mantenimiento.setId(dto.getId());
        mantenimiento.setCamion(crearReferenciaCamion(dto.getCamionId()));
        mantenimiento.setDescripcion(dto.getDescripcion());
        mantenimiento.setFechaProgramada(dto.getFechaProgramada());
        mantenimiento.setFechaRealizada(dto.getFechaRealizada());
        mantenimiento.setCompletado(Boolean.TRUE.equals(dto.getCompletado()));
        return mantenimiento;
    }

    private Camion crearReferenciaCamion(Long id) {
        if (id == null) {
            return null;
        }
        Camion camion = new Camion();
        camion.setId(id);
        return camion;
    }
}
