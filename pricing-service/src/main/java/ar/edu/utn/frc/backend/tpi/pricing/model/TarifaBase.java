package ar.edu.utn.frc.backend.tpi.pricing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tarifas_base")
public class TarifaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoCamion tipoCamion;

    private Double rangoPesoMinKg;
    private Double rangoPesoMaxKg;
    private Double rangoVolumenMinM3;
    private Double rangoVolumenMaxM3;

    private Double costoPorKilometro;
    private Double costoPorKilometroCombustible;

    @Column(name = "costo_fijo_gestion")
    private Double costoFijoGestion;
}
