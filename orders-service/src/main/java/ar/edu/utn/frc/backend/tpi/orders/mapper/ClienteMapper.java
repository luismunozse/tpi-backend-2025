package ar.edu.utn.frc.backend.tpi.orders.mapper;

import ar.edu.utn.frc.backend.tpi.orders.dto.ClienteDto;
import ar.edu.utn.frc.backend.tpi.orders.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public ClienteDto toDto(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return ClienteDto.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .build();
    }

    public Cliente toEntity(ClienteDto dto) {
        if (dto == null) {
            return null;
        }
        Cliente cliente = new Cliente();
        cliente.setId(dto.getId());
        cliente.setNombre(dto.getNombre());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        return cliente;
    }
}
