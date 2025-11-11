package ar.edu.utn.frc.backend.tpi.locations.service;

import ar.edu.utn.frc.backend.tpi.locations.model.Ciudad;
import ar.edu.utn.frc.backend.tpi.locations.model.Coordenada;
import ar.edu.utn.frc.backend.tpi.locations.model.Deposito;
import ar.edu.utn.frc.backend.tpi.locations.model.Provincia;
import ar.edu.utn.frc.backend.tpi.locations.repository.CiudadRepository;
import ar.edu.utn.frc.backend.tpi.locations.repository.DepositoRepository;
import ar.edu.utn.frc.backend.tpi.locations.repository.ProvinciaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepositoService {

    private final DepositoRepository depositoRepository;
    private final CiudadRepository ciudadRepository;
    private final ProvinciaRepository provinciaRepository;

    public List<Deposito> obtenerDepositos() {
        return depositoRepository.findAll();
    }

    public List<Deposito> obtenerDepositosPorCiudad(Integer ciudadId) {
        if (!ciudadRepository.existsById(ciudadId)) {
            throw new EntityNotFoundException("Ciudad no encontrada con id " + ciudadId);
        }
        return depositoRepository.findByCiudadId(ciudadId);
    }

    public Deposito obtenerDepositoPorId(Integer id) {
        return depositoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Depósito no encontrado con id " + id));
    }

    @Transactional
    public Deposito crearDeposito(Deposito deposito) {
        deposito.setCiudad(obtenerCiudad(deposito.getCiudad()));
        deposito.setProvincia(obtenerProvincia(deposito.getProvincia()));
        actualizarCoordenada(deposito, deposito.getCoordenada());
        return depositoRepository.save(deposito);
    }

    @Transactional
    public Deposito actualizarDeposito(Integer id, Deposito datosActualizados) {
        Deposito deposito = obtenerDepositoPorId(id);
        deposito.setNombre(datosActualizados.getNombre());
        deposito.setDireccion(datosActualizados.getDireccion());
        deposito.setAltura(datosActualizados.getAltura());
        deposito.setCiudad(obtenerCiudad(datosActualizados.getCiudad()));
        deposito.setProvincia(obtenerProvincia(datosActualizados.getProvincia()));
        actualizarCoordenada(deposito, datosActualizados.getCoordenada());
        return depositoRepository.save(deposito);
    }

    @Transactional
    public void eliminarDeposito(Integer id) {
        if (!depositoRepository.existsById(id)) {
            throw new EntityNotFoundException("Depósito no encontrado con id " + id);
        }
        depositoRepository.deleteById(id);
    }

    private Ciudad obtenerCiudad(Ciudad ciudad) {
        if (ciudad == null || ciudad.getId() == null) {
            return null;
        }
        return ciudadRepository.findById(ciudad.getId())
                .orElseThrow(() -> new EntityNotFoundException("Ciudad no encontrada con id " + ciudad.getId()));
    }

    private Provincia obtenerProvincia(Provincia provincia) {
        if (provincia == null || provincia.getId() == null) {
            return null;
        }
        return provinciaRepository.findById(provincia.getId())
                .orElseThrow(() -> new EntityNotFoundException("Provincia no encontrada con id " + provincia.getId()));
    }

    private void actualizarCoordenada(Deposito deposito, Coordenada coordenadaNueva) {
        if (coordenadaNueva == null) {
            deposito.setCoordenada(null);
            return;
        }
        if (deposito.getCoordenada() == null) {
            deposito.setCoordenada(coordenadaNueva);
        } else {
            deposito.getCoordenada().setLatitud(coordenadaNueva.getLatitud());
            deposito.getCoordenada().setLongitud(coordenadaNueva.getLongitud());
        }
    }
}
