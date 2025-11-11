package ar.edu.utn.frc.backend.tpi.pricing.service;

import ar.edu.utn.frc.backend.tpi.pricing.model.TarifaDeposito;
import ar.edu.utn.frc.backend.tpi.pricing.repository.TarifaDepositoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TarifaDepositoService {

    private final TarifaDepositoRepository tarifaDepositoRepository;

    public List<TarifaDeposito> obtenerTarifas() {
        return tarifaDepositoRepository.findAll();
    }

    public TarifaDeposito obtenerPorId(Long id) {
        return tarifaDepositoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarifa de depósito no encontrada con id " + id));
    }

    @Transactional
    public TarifaDeposito crear(TarifaDeposito tarifa) {
        validarTarifa(tarifa);
        return tarifaDepositoRepository.save(tarifa);
    }

    @Transactional
    public TarifaDeposito actualizar(Long id, TarifaDeposito datosActualizados) {
        TarifaDeposito tarifa = obtenerPorId(id);
        tarifa.setCostoPorDia(datosActualizados.getCostoPorDia());
        tarifa.setCargoPorIngreso(datosActualizados.getCargoPorIngreso());
        tarifa.setCargoPorSalida(datosActualizados.getCargoPorSalida());
        validarTarifa(tarifa);
        return tarifaDepositoRepository.save(tarifa);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!tarifaDepositoRepository.existsById(id)) {
            throw new EntityNotFoundException("Tarifa de depósito no encontrada con id " + id);
        }
        tarifaDepositoRepository.deleteById(id);
    }

    private void validarTarifa(TarifaDeposito tarifa) {
        if (tarifa.getCostoPorDia() != null && tarifa.getCostoPorDia() < 0) {
            throw new IllegalArgumentException("El costo por día no puede ser negativo");
        }
        if (tarifa.getCargoPorIngreso() != null && tarifa.getCargoPorIngreso() < 0) {
            throw new IllegalArgumentException("El cargo por ingreso no puede ser negativo");
        }
        if (tarifa.getCargoPorSalida() != null && tarifa.getCargoPorSalida() < 0) {
            throw new IllegalArgumentException("El cargo por salida no puede ser negativo");
        }
    }
}
