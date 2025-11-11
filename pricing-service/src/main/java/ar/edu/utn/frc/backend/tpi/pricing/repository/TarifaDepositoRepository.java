package ar.edu.utn.frc.backend.tpi.pricing.repository;

import ar.edu.utn.frc.backend.tpi.pricing.model.TarifaDeposito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarifaDepositoRepository extends JpaRepository<TarifaDeposito, Long> {
}
