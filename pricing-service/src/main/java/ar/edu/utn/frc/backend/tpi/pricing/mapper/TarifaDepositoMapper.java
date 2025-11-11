package ar.edu.utn.frc.backend.tpi.pricing.mapper;

import ar.edu.utn.frc.backend.tpi.pricing.dto.TarifaDepositoDto;
import ar.edu.utn.frc.backend.tpi.pricing.model.TarifaDeposito;
import org.springframework.stereotype.Component;

@Component
public class TarifaDepositoMapper {

    public TarifaDepositoDto toDto(TarifaDeposito tarifa) {
        if (tarifa == null) {
            return null;
        }
        return TarifaDepositoDto.builder()
                .id(tarifa.getId())
                .costoPorDia(tarifa.getCostoPorDia())
                .cargoPorIngreso(tarifa.getCargoPorIngreso())
                .cargoPorSalida(tarifa.getCargoPorSalida())
                .build();
    }

    public TarifaDeposito toEntity(TarifaDepositoDto dto) {
        if (dto == null) {
            return null;
        }
        TarifaDeposito tarifa = new TarifaDeposito();
        tarifa.setId(dto.getId());
        tarifa.setCostoPorDia(dto.getCostoPorDia());
        tarifa.setCargoPorIngreso(dto.getCargoPorIngreso());
        tarifa.setCargoPorSalida(dto.getCargoPorSalida());
        return tarifa;
    }
}
