package ar.edu.utn.frc.backend.tpi.orders.repository;

import ar.edu.utn.frc.backend.tpi.orders.model.SolicitudTransporte;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolicitudTransporteRepository extends JpaRepository<SolicitudTransporte, Long> {

    Optional<SolicitudTransporte> findByNumeroSolicitud(String numeroSolicitud);

    @EntityGraph(attributePaths = {"cliente", "contenedor", "ruta", "ruta.tramos"})
    Optional<SolicitudTransporte> findDetailedById(Long id);

    @EntityGraph(attributePaths = {"cliente", "contenedor"})
    List<SolicitudTransporte> findByEstadoOrderByFechaCreacionDesc(ar.edu.utn.frc.backend.tpi.orders.model.EstadoSolicitud estado);
}
