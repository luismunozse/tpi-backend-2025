package ar.edu.utn.frc.backend.tpi.fleet.mapper;

import ar.edu.utn.frc.backend.tpi.fleet.dto.TransportistaDto;
import ar.edu.utn.frc.backend.tpi.fleet.model.Transportista;
import org.springframework.stereotype.Component;

@Component
public class TransportistaMapper {

    public TransportistaDto toDto(Transportista transportista) {
        if (transportista == null) {
            return null;
        }
        return TransportistaDto.builder()
                .id(transportista.getId())
                .nombre(transportista.getNombre())
                .apellido(transportista.getApellido())
                .telefono(transportista.getTelefono())
                .email(transportista.getEmail())
                .build();
    }

    public Transportista toEntity(TransportistaDto dto) {
        if (dto == null) {
            return null;
        }
        Transportista transportista = new Transportista();
        transportista.setId(dto.getId());
        transportista.setNombre(dto.getNombre());
        transportista.setApellido(dto.getApellido());
        transportista.setTelefono(dto.getTelefono());
        transportista.setEmail(dto.getEmail());
        return transportista;
    }
}
