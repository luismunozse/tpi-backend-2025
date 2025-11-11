package ar.edu.utn.frc.backend.tpi.pricing.mapper;

import ar.edu.utn.frc.backend.tpi.pricing.dto.RecargoTarifaDto;
import ar.edu.utn.frc.backend.tpi.pricing.model.RecargoTarifa;
import org.springframework.stereotype.Component;

@Component
public class RecargoTarifaMapper {

    public RecargoTarifaDto toDto(RecargoTarifa recargo) {
        if (recargo == null) {
            return null;
        }
        return RecargoTarifaDto.builder()
                .id(recargo.getId())
                .descripcion(recargo.getDescripcion())
                .porcentaje(recargo.getPorcentaje())
                .montoFijo(recargo.getMontoFijo())
                .build();
    }

    public RecargoTarifa toEntity(RecargoTarifaDto dto) {
        if (dto == null) {
            return null;
        }
        RecargoTarifa recargo = new RecargoTarifa();
        recargo.setId(dto.getId());
        recargo.setDescripcion(dto.getDescripcion());
        recargo.setPorcentaje(dto.getPorcentaje());
        recargo.setMontoFijo(dto.getMontoFijo());
        return recargo;
    }
}
