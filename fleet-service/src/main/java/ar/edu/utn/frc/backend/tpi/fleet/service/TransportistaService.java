package ar.edu.utn.frc.backend.tpi.fleet.service;

import ar.edu.utn.frc.backend.tpi.fleet.model.Transportista;
import ar.edu.utn.frc.backend.tpi.fleet.repository.TransportistaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransportistaService {

    private final TransportistaRepository transportistaRepository;

    public List<Transportista> obtenerTransportistas() {
        return transportistaRepository.findAll();
    }

    public Transportista obtenerTransportistaPorId(Long id) {
        return transportistaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transportista no encontrado con id " + id));
    }

    @Transactional
    public Transportista crearTransportista(Transportista transportista) {
        validarEmailDisponible(transportista.getEmail(), null);
        return transportistaRepository.save(transportista);
    }

    @Transactional
    public Transportista actualizarTransportista(Long id, Transportista datosActualizados) {
        Transportista transportista = obtenerTransportistaPorId(id);
        if (datosActualizados.getEmail() != null && !datosActualizados.getEmail().equalsIgnoreCase(transportista.getEmail())) {
            validarEmailDisponible(datosActualizados.getEmail(), id);
            transportista.setEmail(datosActualizados.getEmail());
        }
        transportista.setNombre(datosActualizados.getNombre());
        transportista.setApellido(datosActualizados.getApellido());
        transportista.setTelefono(datosActualizados.getTelefono());
        return transportistaRepository.save(transportista);
    }

    @Transactional
    public void eliminarTransportista(Long id) {
        if (!transportistaRepository.existsById(id)) {
            throw new EntityNotFoundException("Transportista no encontrado con id " + id);
        }
        transportistaRepository.deleteById(id);
    }

    private void validarEmailDisponible(String email, Long idActual) {
        if (email == null || email.isBlank()) {
            return;
        }
        transportistaRepository.findByEmailIgnoreCase(email)
                .filter(existing -> !existing.getId().equals(idActual))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ya existe un transportista con el email " + email);
                });
    }
}
