package ar.edu.utn.frc.backend.tpi.locations.repository;

import ar.edu.utn.frc.backend.tpi.locations.model.Ruta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RutaRepository extends JpaRepository<Ruta, Long> {

    @EntityGraph(attributePaths = {
            "tramos",
            "tramos.origenCoordenada",
            "tramos.destinoCoordenada",
            "tramos.origenDeposito",
            "tramos.destinoDeposito"
    })
    List<Ruta> findAllWithDetalles();

    @EntityGraph(attributePaths = {
            "tramos",
            "tramos.origenCoordenada",
            "tramos.destinoCoordenada",
            "tramos.origenDeposito",
            "tramos.destinoDeposito"
    })
    Optional<Ruta> findDetailedById(Long id);
}
