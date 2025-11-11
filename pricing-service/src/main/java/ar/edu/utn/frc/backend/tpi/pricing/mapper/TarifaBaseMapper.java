package ar.edu.utn.frc.backend.tpi.pricing.mapper;

import ar.edu.utn.frc.backend.tpi.pricing.dto.TarifaBaseDto;
import ar.edu.utn.frc.backend.tpi.pricing.model.TarifaBase;
import ar.edu.utn.frc.backend.tpi.pricing.model.TipoCamion;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TarifaBaseMapper {

    public TarifaBaseDto toDto(TarifaBase tarifa) {
        if (tarifa == null) {
            return null;
        }
        return TarifaBaseDto.builder()
                .id(tarifa.getId())
                .tipoCamion(tarifa.getTipoCamion() != null ? tarifa.getTipoCamion().name() : null)
                .rangoPesoMinKg(tarifa.getRangoPesoMinKg())
                .rangoPesoMaxKg(tarifa.getRangoPesoMaxKg())
                .rangoVolumenMinM3(tarifa.getRangoVolumenMinM3())
                .rangoVolumenMaxM3(tarifa.getRangoVolumenMaxM3())
                .costoPorKilometro(tarifa.getCostoPorKilometro())
                .costoPorKilometroCombustible(tarifa.getCostoPorKilometroCombustible())
                .costoFijoGestion(tarifa.getCostoFijoGestion())
                .build();
    }

    public TarifaBase toEntity(TarifaBaseDto dto) {
        if (dto == null) {
            return null;
        }
        TarifaBase tarifa = new TarifaBase();
        tarifa.setId(dto.getId());
        tarifa.setTipoCamion(parseTipoCamion(dto.getTipoCamion()));
        tarifa.setRangoPesoMinKg(dto.getRangoPesoMinKg());
        tarifa.setRangoPesoMaxKg(dto.getRangoPesoMaxKg());
        tarifa.setRangoVolumenMinM3(dto.getRangoVolumenMinM3());
        tarifa.setRangoVolumenMaxM3(dto.getRangoVolumenMaxM3());
        tarifa.setCostoPorKilometro(dto.getCostoPorKilometro());
        tarifa.setCostoPorKilometroCombustible(dto.getCostoPorKilometroCombustible());
        tarifa.setCostoFijoGestion(dto.getCostoFijoGestion());
        return tarifa;
    }

    private TipoCamion parseTipoCamion(String tipo) {
        if (tipo == null) {
            return null;
        }
        return TipoCamion.valueOf(tipo.toUpperCase(Locale.ROOT));
    }
}
