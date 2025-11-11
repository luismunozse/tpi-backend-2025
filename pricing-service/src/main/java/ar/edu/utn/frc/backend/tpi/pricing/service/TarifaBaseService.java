package ar.edu.utn.frc.backend.tpi.pricing.service;

import ar.edu.utn.frc.backend.tpi.pricing.model.TarifaBase;
import ar.edu.utn.frc.backend.tpi.pricing.model.TipoCamion;
import ar.edu.utn.frc.backend.tpi.pricing.repository.TarifaBaseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TarifaBaseService {

    private final TarifaBaseRepository tarifaBaseRepository;

    public List<TarifaBase> obtenerTarifas() {
        return tarifaBaseRepository.findAll();
    }

    public List<TarifaBase> obtenerTarifasPorTipo(TipoCamion tipoCamion) {
        return tarifaBaseRepository.findByTipoCamionOrderByRangoPesoMinKgAsc(tipoCamion);
    }

    public TarifaBase obtenerPorId(Long id) {
        return tarifaBaseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarifa base no encontrada con id " + id));
    }

    @Transactional
    public TarifaBase crear(TarifaBase tarifaBase) {
        validarRangos(tarifaBase);
        return tarifaBaseRepository.save(tarifaBase);
    }

    @Transactional
    public TarifaBase actualizar(Long id, TarifaBase datosActualizados) {
        TarifaBase tarifa = obtenerPorId(id);
        tarifa.setTipoCamion(datosActualizados.getTipoCamion());
        tarifa.setRangoPesoMinKg(datosActualizados.getRangoPesoMinKg());
        tarifa.setRangoPesoMaxKg(datosActualizados.getRangoPesoMaxKg());
        tarifa.setRangoVolumenMinM3(datosActualizados.getRangoVolumenMinM3());
        tarifa.setRangoVolumenMaxM3(datosActualizados.getRangoVolumenMaxM3());
        tarifa.setCostoPorKilometro(datosActualizados.getCostoPorKilometro());
        tarifa.setCostoPorKilometroCombustible(datosActualizados.getCostoPorKilometroCombustible());
        tarifa.setCostoFijoGestion(datosActualizados.getCostoFijoGestion());

        validarRangos(tarifa);

        return tarifaBaseRepository.save(tarifa);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!tarifaBaseRepository.existsById(id)) {
            throw new EntityNotFoundException("Tarifa base no encontrada con id " + id);
        }
        tarifaBaseRepository.deleteById(id);
    }

    private void validarRangos(TarifaBase tarifa) {
        if (tarifa.getRangoPesoMinKg() != null && tarifa.getRangoPesoMaxKg() != null
                && tarifa.getRangoPesoMinKg() > tarifa.getRangoPesoMaxKg()) {
            throw new IllegalArgumentException("El rango de peso es inválido");
        }
        if (tarifa.getRangoVolumenMinM3() != null && tarifa.getRangoVolumenMaxM3() != null
                && tarifa.getRangoVolumenMinM3() > tarifa.getRangoVolumenMaxM3()) {
            throw new IllegalArgumentException("El rango de volumen es inválido");
        }
    }
}
