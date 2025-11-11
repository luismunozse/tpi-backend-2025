package ar.edu.utn.frc.backend.tpi.pricing.repository;

import ar.edu.utn.frc.backend.tpi.pricing.model.RecargoTarifa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecargoTarifaRepository extends JpaRepository<RecargoTarifa, Long> {
}
