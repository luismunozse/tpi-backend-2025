package ar.edu.utn.frc.backend.tpi.fleet.repository;

import ar.edu.utn.frc.backend.tpi.fleet.model.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    List<Mantenimiento> findByCamionId(Long camionId);
}
