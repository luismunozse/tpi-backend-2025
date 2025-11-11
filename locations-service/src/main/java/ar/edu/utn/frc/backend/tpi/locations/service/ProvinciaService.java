package ar.edu.utn.frc.backend.tpi.locations.service;

import ar.edu.utn.frc.backend.tpi.locations.model.Provincia;
import ar.edu.utn.frc.backend.tpi.locations.repository.ProvinciaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProvinciaService {

    private final ProvinciaRepository provinciaRepository;

    public List<Provincia> obtenerProvincias() {
        return provinciaRepository.findAll();
    }

    public Provincia obtenerProvinciaPorId(Integer id) {
        return provinciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Provincia no encontrada con id " + id));
    }

    @Transactional
    public Provincia crearProvincia(Provincia provincia) {
        return provinciaRepository.save(provincia);
    }

    @Transactional
    public Provincia actualizarProvincia(Integer id, Provincia datosActualizados) {
        Provincia provincia = obtenerProvinciaPorId(id);
        provincia.setNombre(datosActualizados.getNombre());
        return provinciaRepository.save(provincia);
    }

    @Transactional
    public void eliminarProvincia(Integer id) {
        if (!provinciaRepository.existsById(id)) {
            throw new EntityNotFoundException("Provincia no encontrada con id " + id);
        }
        provinciaRepository.deleteById(id);
    }
}
