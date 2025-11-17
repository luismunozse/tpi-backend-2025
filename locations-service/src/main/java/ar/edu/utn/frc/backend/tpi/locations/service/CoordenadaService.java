package ar.edu.utn.frc.backend.tpi.locations.service;

import ar.edu.utn.frc.backend.tpi.locations.model.Coordenada;
import ar.edu.utn.frc.backend.tpi.locations.repository.CoordenadaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoordenadaService {

    private final CoordenadaRepository coordenadaRepository;

    public List<Coordenada> obtenerCoordenadas() {
        return coordenadaRepository.findAll();
    }

    public Coordenada obtenerCoordenadaPorId(Long id) {
        return coordenadaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coordenada no encontrada con id " + id));
    }

    @Transactional
    public Coordenada crearCoordenada(Coordenada coordenada) {
        return coordenadaRepository.save(coordenada);
    }

    @Transactional
    public Coordenada actualizarCoordenada(Long id, Coordenada datosActualizados) {
        Coordenada coordenada = obtenerCoordenadaPorId(id);
        coordenada.setLatitud(datosActualizados.getLatitud());
        coordenada.setLongitud(datosActualizados.getLongitud());
        return coordenadaRepository.save(coordenada);
    }

    @Transactional
    public void eliminarCoordenada(Long id) {
        if (!coordenadaRepository.existsById(id)) {
            throw new EntityNotFoundException("Coordenada no encontrada con id " + id);
        }
        coordenadaRepository.deleteById(id);
    }
}
