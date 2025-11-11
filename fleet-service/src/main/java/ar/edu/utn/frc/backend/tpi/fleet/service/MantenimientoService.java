package ar.edu.utn.frc.backend.tpi.fleet.service;

import ar.edu.utn.frc.backend.tpi.fleet.model.Camion;
import ar.edu.utn.frc.backend.tpi.fleet.model.EstadoCamion;
import ar.edu.utn.frc.backend.tpi.fleet.model.Mantenimiento;
import ar.edu.utn.frc.backend.tpi.fleet.repository.CamionRepository;
import ar.edu.utn.frc.backend.tpi.fleet.repository.MantenimientoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final CamionRepository camionRepository;

    public List<Mantenimiento> obtenerMantenimientos() {
        return mantenimientoRepository.findAll();
    }

    public List<Mantenimiento> obtenerMantenimientosPorCamion(Long camionId) {
        return mantenimientoRepository.findByCamionId(camionId);
    }

    public Mantenimiento obtenerMantenimientoPorId(Long id) {
        return mantenimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mantenimiento no encontrado con id " + id));
    }

    @Transactional
    public Mantenimiento crearMantenimiento(Mantenimiento mantenimiento) {
        Camion camion = obtenerCamion(mantenimiento.getCamion());
        mantenimiento.setCamion(camion);
        if (mantenimiento.getFechaProgramada() == null) {
            mantenimiento.setFechaProgramada(LocalDate.now());
        }
        actualizarEstadoCamionSegunMantenimiento(camion, mantenimiento.isCompletado());
        return mantenimientoRepository.save(mantenimiento);
    }

    @Transactional
    public Mantenimiento actualizarMantenimiento(Long id, Mantenimiento datosActualizados) {
        Mantenimiento mantenimiento = obtenerMantenimientoPorId(id);

        if (datosActualizados.getCamion() != null && datosActualizados.getCamion().getId() != null
                && !datosActualizados.getCamion().getId().equals(mantenimiento.getCamion().getId())) {
            Camion camion = obtenerCamion(datosActualizados.getCamion());
            mantenimiento.setCamion(camion);
        }

        mantenimiento.setDescripcion(datosActualizados.getDescripcion());
        mantenimiento.setFechaProgramada(datosActualizados.getFechaProgramada());
        mantenimiento.setFechaRealizada(datosActualizados.getFechaRealizada());
        mantenimiento.setCompletado(datosActualizados.isCompletado());

        actualizarEstadoCamionSegunMantenimiento(mantenimiento.getCamion(), mantenimiento.isCompletado());

        return mantenimientoRepository.save(mantenimiento);
    }

    @Transactional
    public void eliminarMantenimiento(Long id) {
        Mantenimiento mantenimiento = obtenerMantenimientoPorId(id);
        mantenimientoRepository.delete(mantenimiento);
        // si se elimina el mantenimiento y el camión estaba en mantenimiento, devolver a disponible
        Camion camion = mantenimiento.getCamion();
        if (camion != null && camion.getEstado() == EstadoCamion.EN_MANTENIMIENTO) {
            camion.setEstado(EstadoCamion.DISPONIBLE);
            camionRepository.save(camion);
        }
    }

    private Camion obtenerCamion(Camion camion) {
        if (camion == null || camion.getId() == null) {
            throw new IllegalArgumentException("El mantenimiento debe asociarse a un camión existente");
        }
        return camionRepository.findById(camion.getId())
                .orElseThrow(() -> new EntityNotFoundException("Camión no encontrado con id " + camion.getId()));
    }

    private void actualizarEstadoCamionSegunMantenimiento(Camion camion, boolean mantenimientoCompletado) {
        if (camion == null) {
            return;
        }
        if (mantenimientoCompletado) {
            camion.setEstado(EstadoCamion.DISPONIBLE);
        } else {
            camion.setEstado(EstadoCamion.EN_MANTENIMIENTO);
        }
        camionRepository.save(camion);
    }
}
