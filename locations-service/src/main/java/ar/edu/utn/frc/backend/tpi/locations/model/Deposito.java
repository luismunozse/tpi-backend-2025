package ar.edu.utn.frc.backend.tpi.locations.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "depositos")
public class Deposito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String direccion;
    private int altura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ciudad_id")
    private Ciudad ciudad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provincia_id")
    private Provincia provincia;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "coordenada_id")
    private Coordenada coordenada;
    
}
