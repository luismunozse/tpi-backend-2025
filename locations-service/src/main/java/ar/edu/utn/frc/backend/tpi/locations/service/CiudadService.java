package ar.edu.utn.frc.backend.tpi.locations.service;

import ar.edu.utn.frc.backend.tpi.locations.model.Ciudad;
import ar.edu.utn.frc.backend.tpi.locations.model.Coordenada;
import ar.edu.utn.frc.backend.tpi.locations.model.Provincia;
import ar.edu.utn.frc.backend.tpi.locations.repository.CiudadRepository;
import ar.edu.utn.frc.backend.tpi.locations.repository.ProvinciaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CiudadService {

    private final CiudadRepository ciudadRepository;
    private final ProvinciaRepository provinciaRepository;

    public List<Ciudad> obtenerCiudades() {
        return ciudadRepository.findAll();
    }

    public Ciudad obtenerCiudadPorId(Long id) {
        return ciudadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ciudad no encontrada con id " + id));
    }

    @Transactional
    public Ciudad crearCiudad(Ciudad ciudad) {
        ciudad.setProvincia(obtenerProvincia(ciudad.getProvincia()));
        actualizarCoordenada(ciudad, ciudad.getCoordenada());
        return ciudadRepository.save(ciudad);
    }

    @Transactional
    public Ciudad actualizarCiudad(Long id, Ciudad datosActualizados) {
        Ciudad ciudad = obtenerCiudadPorId(id);
        ciudad.setNombre(datosActualizados.getNombre());
        ciudad.setProvincia(obtenerProvincia(datosActualizados.getProvincia()));
        actualizarCoordenada(ciudad, datosActualizados.getCoordenada());
        return ciudadRepository.save(ciudad);
    }

    @Transactional
    public void eliminarCiudad(Long id) {
        if (!ciudadRepository.existsById(id)) {
            throw new EntityNotFoundException("Ciudad no encontrada con id " + id);
        }
        ciudadRepository.deleteById(id);
    }

    private Provincia obtenerProvincia(Provincia provincia) {
        if (provincia == null || provincia.getId() == null) {
            return null;
        }
        return provinciaRepository.findById(provincia.getId())
                .orElseThrow(() -> new EntityNotFoundException("Provincia no encontrada con id " + provincia.getId()));
    }

    private void actualizarCoordenada(Ciudad ciudad, Coordenada coordenadaNueva) {
        if (coordenadaNueva == null) {
            ciudad.setCoordenada(null);
            return;
        }
        if (ciudad.getCoordenada() == null) {
            ciudad.setCoordenada(coordenadaNueva);
        } else {
            ciudad.getCoordenada().setLatitud(coordenadaNueva.getLatitud());
            ciudad.getCoordenada().setLongitud(coordenadaNueva.getLongitud());
        }
    }
}
