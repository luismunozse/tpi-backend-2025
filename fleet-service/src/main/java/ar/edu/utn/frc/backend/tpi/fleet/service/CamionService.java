package ar.edu.utn.frc.backend.tpi.fleet.service;

import ar.edu.utn.frc.backend.tpi.fleet.model.Camion;
import ar.edu.utn.frc.backend.tpi.fleet.model.EstadoCamion;
import ar.edu.utn.frc.backend.tpi.fleet.model.Transportista;
import ar.edu.utn.frc.backend.tpi.fleet.repository.CamionRepository;
import ar.edu.utn.frc.backend.tpi.fleet.repository.TransportistaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CamionService {

    private final CamionRepository camionRepository;
    private final TransportistaRepository transportistaRepository;

    public List<Camion> obtenerCamiones() {
        return camionRepository.findAll();
    }

    public List<Camion> obtenerCamionesPorEstado(EstadoCamion estado) {
        return camionRepository.findByEstado(estado);
    }

    public List<Camion> buscarDisponiblesPorCapacidad(double pesoRequerido, double volumenRequerido) {
        return camionRepository.findByEstadoAndCapacidadPesoKgGreaterThanEqualAndCapacidadVolumenM3GreaterThanEqual(
                EstadoCamion.DISPONIBLE,
                pesoRequerido,
                volumenRequerido
        );
    }

    public Camion obtenerCamionPorId(Long id) {
        return camionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Camión no encontrado con id " + id));
    }

    @Transactional
    public Camion crearCamion(Camion camion) {
        validarDominio(camion.getDominio(), null);
        camion.setTransportista(resolverTransportista(camion.getTransportista()));
        if (camion.getEstado() == null) {
            camion.setEstado(EstadoCamion.DISPONIBLE);
        }
        validarCapacidad(camion.getCapacidadPesoKg(), camion.getCapacidadVolumenM3());
        return camionRepository.save(camion);
    }

    @Transactional
    public Camion actualizarCamion(Long id, Camion datosActualizados) {
        Camion camion = obtenerCamionPorId(id);

        if (datosActualizados.getDominio() != null && !datosActualizados.getDominio().equalsIgnoreCase(camion.getDominio())) {
            validarDominio(datosActualizados.getDominio(), id);
            camion.setDominio(datosActualizados.getDominio());
        }

        camion.setCapacidadPesoKg(datosActualizados.getCapacidadPesoKg());
        camion.setCapacidadVolumenM3(datosActualizados.getCapacidadVolumenM3());
        camion.setConsumoCombustiblePorKm(datosActualizados.getConsumoCombustiblePorKm());
        camion.setCostoBasePorKm(datosActualizados.getCostoBasePorKm());
        camion.setEstado(datosActualizados.getEstado() != null ? datosActualizados.getEstado() : camion.getEstado());
        camion.setTransportista(resolverTransportista(datosActualizados.getTransportista()));

        validarCapacidad(camion.getCapacidadPesoKg(), camion.getCapacidadVolumenM3());

        return camionRepository.save(camion);
    }

    @Transactional
    public Camion actualizarEstado(Long id, String estado) {
        Camion camion = obtenerCamionPorId(id);
        camion.setEstado(parseEstado(estado));
        return camionRepository.save(camion);
    }

    @Transactional
    public void eliminarCamion(Long id) {
        if (!camionRepository.existsById(id)) {
            throw new EntityNotFoundException("Camión no encontrado con id " + id);
        }
        camionRepository.deleteById(id);
    }

    private void validarDominio(String dominio, Long idActual) {
        if (dominio == null || dominio.isBlank()) {
            throw new IllegalArgumentException("El dominio del camión es obligatorio");
        }
        camionRepository.findByDominioIgnoreCase(dominio)
                .filter(existing -> !existing.getId().equals(idActual))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ya existe un camión con dominio " + dominio);
                });
    }

    private void validarCapacidad(Double peso, Double volumen) {
        if (peso != null && peso <= 0) {
            throw new IllegalArgumentException("La capacidad de peso debe ser mayor a cero");
        }
        if (volumen != null && volumen <= 0) {
            throw new IllegalArgumentException("La capacidad de volumen debe ser mayor a cero");
        }
    }

    private Transportista resolverTransportista(Transportista transportista) {
        if (transportista == null || transportista.getId() == null) {
            return null;
        }
        return transportistaRepository.findById(transportista.getId())
                .orElseThrow(() -> new EntityNotFoundException("Transportista no encontrado con id " + transportista.getId()));
    }

    private EstadoCamion parseEstado(String estado) {
        if (estado == null) {
            return EstadoCamion.DISPONIBLE;
        }
        return EstadoCamion.valueOf(estado.toUpperCase(Locale.ROOT));
    }
}
