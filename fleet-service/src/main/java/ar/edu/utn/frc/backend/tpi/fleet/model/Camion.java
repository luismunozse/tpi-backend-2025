package ar.edu.utn.frc.backend.tpi.fleet.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "camiones")
public class Camion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String dominio;

    private Double capacidadPesoKg;
    private Double capacidadVolumenM3;
    private Double consumoCombustiblePorKm;
    private Double costoBasePorKm;

    @Enumerated(EnumType.STRING)
    private EstadoCamion estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportista_id")
    private Transportista transportista;

    @OneToMany(mappedBy = "camion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mantenimiento> mantenimientos = new ArrayList<>();

    public boolean puedeTransportar(Double peso, Double volumen) {
        if (peso != null && capacidadPesoKg != null && peso > capacidadPesoKg) {
            return false;
        }
        if (volumen != null && capacidadVolumenM3 != null && volumen > capacidadVolumenM3) {
            return false;
        }
        return true;
    }
}
