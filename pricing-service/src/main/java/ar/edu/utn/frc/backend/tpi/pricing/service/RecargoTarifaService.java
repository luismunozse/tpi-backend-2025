package ar.edu.utn.frc.backend.tpi.pricing.service;

import ar.edu.utn.frc.backend.tpi.pricing.model.RecargoTarifa;
import ar.edu.utn.frc.backend.tpi.pricing.repository.RecargoTarifaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecargoTarifaService {

    private final RecargoTarifaRepository recargoTarifaRepository;

    public List<RecargoTarifa> obtenerRecargos() {
        return recargoTarifaRepository.findAll();
    }

    public RecargoTarifa obtenerPorId(Long id) {
        return recargoTarifaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recargo no encontrado con id " + id));
    }

    @Transactional
    public RecargoTarifa crear(RecargoTarifa recargo) {
        validarRecargo(recargo);
        return recargoTarifaRepository.save(recargo);
    }

    @Transactional
    public RecargoTarifa actualizar(Long id, RecargoTarifa datosActualizados) {
        RecargoTarifa recargo = obtenerPorId(id);
        recargo.setDescripcion(datosActualizados.getDescripcion());
        recargo.setMontoFijo(datosActualizados.getMontoFijo());
        recargo.setPorcentaje(datosActualizados.getPorcentaje());
        validarRecargo(recargo);
        return recargoTarifaRepository.save(recargo);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!recargoTarifaRepository.existsById(id)) {
            throw new EntityNotFoundException("Recargo no encontrado con id " + id);
        }
        recargoTarifaRepository.deleteById(id);
    }

    private void validarRecargo(RecargoTarifa recargo) {
        if (recargo.getPorcentaje() != null && recargo.getPorcentaje() < 0) {
            throw new IllegalArgumentException("El porcentaje no puede ser negativo");
        }
        if (recargo.getMontoFijo() != null && recargo.getMontoFijo() < 0) {
            throw new IllegalArgumentException("El monto fijo no puede ser negativo");
        }
    }
}
