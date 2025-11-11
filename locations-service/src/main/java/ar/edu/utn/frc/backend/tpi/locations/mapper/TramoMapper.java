package ar.edu.utn.frc.backend.tpi.locations.mapper;

import ar.edu.utn.frc.backend.tpi.locations.dto.TramoDto;
import ar.edu.utn.frc.backend.tpi.locations.model.Coordenada;
import ar.edu.utn.frc.backend.tpi.locations.model.Deposito;
import ar.edu.utn.frc.backend.tpi.locations.model.TipoTramo;
import ar.edu.utn.frc.backend.tpi.locations.model.Tramo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class TramoMapper {

    private final CoordenadaMapper coordenadaMapper;

    public TramoDto toDto(Tramo tramo) {
        if (tramo == null) {
            return null;
        }
        return TramoDto.builder()
                .id(tramo.getId())
                .orden(tramo.getOrden())
                .tipo(tramo.getTipo() != null ? tramo.getTipo().name() : null)
                .distanciaEstimadaKm(tramo.getDistanciaEstimadaKm())
                .origen(coordenadaMapper.toDto(tramo.getOrigenCoordenada()))
                .destino(coordenadaMapper.toDto(tramo.getDestinoCoordenada()))
                .origenDepositoId(tramo.getOrigenDeposito() != null ? tramo.getOrigenDeposito().getId() : null)
                .destinoDepositoId(tramo.getDestinoDeposito() != null ? tramo.getDestinoDeposito().getId() : null)
                .build();
    }

    public Tramo toEntity(TramoDto dto) {
        if (dto == null) {
            return null;
        }
        Tramo tramo = new Tramo();
        tramo.setId(dto.getId());
        tramo.setOrden(dto.getOrden());
        tramo.setTipo(mapearTipo(dto.getTipo()));
        tramo.setDistanciaEstimadaKm(dto.getDistanciaEstimadaKm());
        tramo.setOrigenCoordenada(coordenadaMapper.toEntity(dto.getOrigen()));
        tramo.setDestinoCoordenada(coordenadaMapper.toEntity(dto.getDestino()));
        tramo.setOrigenDeposito(crearReferenciaDeposito(dto.getOrigenDepositoId()));
        tramo.setDestinoDeposito(crearReferenciaDeposito(dto.getDestinoDepositoId()));
        return tramo;
    }

    private TipoTramo mapearTipo(String tipo) {
        if (tipo == null) {
            return null;
        }
        return TipoTramo.valueOf(tipo.toUpperCase(Locale.ROOT));
    }

    private Deposito crearReferenciaDeposito(Integer id) {
        if (id == null) {
            return null;
        }
        Deposito deposito = new Deposito();
        deposito.setId(id);
        return deposito;
    }
}
