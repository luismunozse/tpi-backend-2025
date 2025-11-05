package ar.edu.utn.frc.backend.tpi.locations.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private int id;
    private String nombre;
    private String direccion;
    private int altura;
    private Ciudad ciudad;
    private Provincia provincia;
    
}
