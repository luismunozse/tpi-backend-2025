package ar.edu.utn.frc.backend.tpi.locations.service;

import ar.edu.utn.frc.backend.tpi.locations.model.Deposito;
import ar.edu.utn.frc.backend.tpi.locations.model.Ruta;
import ar.edu.utn.frc.backend.tpi.locations.model.Tramo;
import ar.edu.utn.frc.backend.tpi.locations.repository.DepositoRepository;
import ar.edu.utn.frc.backend.tpi.locations.repository.RutaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RutaService {

    private final RutaRepository rutaRepository;
    private final DepositoRepository depositoRepository;

    public List<Ruta> obtenerRutas() {
        List<Ruta> rutas = rutaRepository.findAllWithDetalles();
        rutas.forEach(this::hidratarTramos);
        return rutas;
    }

    public Ruta obtenerRutaPorId(Long id) {
        Ruta ruta = rutaRepository.findDetailedById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada con id " + id));
        hidratarTramos(ruta);
        return ruta;
    }

    @Transactional
    public Ruta crearRuta(Ruta ruta) {
        prepararTramos(ruta);
        return rutaRepository.save(ruta);
    }

    @Transactional
    public Ruta actualizarRuta(Long id, Ruta datosActualizados) {
        Ruta ruta = obtenerRutaPorId(id);
        ruta.setNombre(datosActualizados.getNombre());
        ruta.setDescripcion(datosActualizados.getDescripcion());

        ruta.getTramos().clear();
        if (datosActualizados.getTramos() != null) {
            datosActualizados.getTramos().forEach(tramo -> {
                tramo.setRuta(ruta);
                prepararTramo(tramo);
                ruta.getTramos().add(tramo);
            });
        }
        return rutaRepository.save(ruta);
    }

    @Transactional
    public void eliminarRuta(Long id) {
        if (!rutaRepository.existsById(id)) {
            throw new EntityNotFoundException("Ruta no encontrada con id " + id);
        }
        rutaRepository.deleteById(id);
    }

    private void prepararTramos(Ruta ruta) {
        if (ruta.getTramos() == null) {
            return;
        }
        ruta.getTramos().forEach(tramo -> {
            tramo.setRuta(ruta);
            prepararTramo(tramo);
        });
    }

    private void prepararTramo(Tramo tramo) {
        tramo.setOrigenDeposito(resolverDeposito(tramo.getOrigenDeposito()));
        tramo.setDestinoDeposito(resolverDeposito(tramo.getDestinoDeposito()));
    }

    private Deposito resolverDeposito(Deposito deposito) {
        if (deposito == null || deposito.getId() == null) {
            return null;
        }
        return depositoRepository.findById(deposito.getId())
                .orElseThrow(() -> new EntityNotFoundException("Depósito no encontrado con id " + deposito.getId()));
    }

    private void hidratarTramos(Ruta ruta) {
        ruta.getTramos().forEach(tramo -> {
            if (tramo.getOrigenCoordenada() != null) {
                tramo.getOrigenCoordenada().getLatitud();
            }
            if (tramo.getDestinoCoordenada() != null) {
                tramo.getDestinoCoordenada().getLatitud();
            }
            if (tramo.getOrigenDeposito() != null) {
                tramo.getOrigenDeposito().getNombre();
            }
            if (tramo.getDestinoDeposito() != null) {
                tramo.getDestinoDeposito().getNombre();
            }
        });
    }
}
