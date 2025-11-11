package ar.edu.utn.frc.backend.tpi.locations.mapper;

import ar.edu.utn.frc.backend.tpi.locations.dto.DepositoDto;
import ar.edu.utn.frc.backend.tpi.locations.model.Deposito;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepositoMapper {

    private final CiudadMapper ciudadMapper;
    private final ProvinciaMapper provinciaMapper;
    private final CoordenadaMapper coordenadaMapper;

    public DepositoDto toDto(Deposito deposito) {
        if (deposito == null) {
            return null;
        }
        return DepositoDto.builder()
                .id(deposito.getId())
                .nombre(deposito.getNombre())
                .direccion(deposito.getDireccion())
                .altura(deposito.getAltura())
                .ciudad(ciudadMapper.toDto(deposito.getCiudad()))
                .provincia(provinciaMapper.toDto(deposito.getProvincia()))
                .coordenada(coordenadaMapper.toDto(deposito.getCoordenada()))
                .build();
    }

    public Deposito toEntity(DepositoDto dto) {
        if (dto == null) {
            return null;
        }
        Deposito deposito = new Deposito();
        deposito.setId(dto.getId());
        deposito.setNombre(dto.getNombre());
        deposito.setDireccion(dto.getDireccion());
        deposito.setAltura(dto.getAltura());
        deposito.setCiudad(ciudadMapper.toEntity(dto.getCiudad()));
        deposito.setProvincia(provinciaMapper.toEntity(dto.getProvincia()));
        deposito.setCoordenada(coordenadaMapper.toEntity(dto.getCoordenada()));
        return deposito;
    }
}
