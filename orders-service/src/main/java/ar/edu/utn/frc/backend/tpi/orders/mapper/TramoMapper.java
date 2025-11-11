package ar.edu.utn.frc.backend.tpi.orders.mapper;

import ar.edu.utn.frc.backend.tpi.orders.dto.TramoDto;
import ar.edu.utn.frc.backend.tpi.orders.model.EstadoTramo;
import ar.edu.utn.frc.backend.tpi.orders.model.TipoTramo;
import ar.edu.utn.frc.backend.tpi.orders.model.Tramo;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TramoMapper {

    public TramoDto toDto(Tramo tramo) {
        if (tramo == null) {
            return null;
        }
        return TramoDto.builder()
                .id(tramo.getId())
                .orden(tramo.getOrden())
                .tipo(tramo.getTipo() != null ? tramo.getTipo().name() : null)
                .estado(tramo.getEstado() != null ? tramo.getEstado().name() : null)
                .origenNombre(tramo.getOrigenNombre())
                .destinoNombre(tramo.getDestinoNombre())
                .origenLatitud(tramo.getOrigenLatitud())
                .origenLongitud(tramo.getOrigenLongitud())
                .destinoLatitud(tramo.getDestinoLatitud())
                .destinoLongitud(tramo.getDestinoLongitud())
                .distanciaEstimadaKm(tramo.getDistanciaEstimadaKm())
                .duracionEstimadaHoras(tramo.getDuracionEstimadaHoras())
                .costoEstimado(tramo.getCostoEstimado())
                .costoReal(tramo.getCostoReal())
                .camionAsignadoId(tramo.getCamionAsignadoId())
                .fechaEstimadaInicio(tramo.getFechaEstimadaInicio())
                .fechaEstimadaFin(tramo.getFechaEstimadaFin())
                .fechaRealInicio(tramo.getFechaRealInicio())
                .fechaRealFin(tramo.getFechaRealFin())
                .build();
    }

    public Tramo toEntity(TramoDto dto) {
        if (dto == null) {
            return null;
        }
        Tramo tramo = new Tramo();
        tramo.setId(dto.getId());
        tramo.setOrden(dto.getOrden());
        tramo.setTipo(parseTipo(dto.getTipo()));
        tramo.setEstado(parseEstado(dto.getEstado()));
        tramo.setOrigenNombre(dto.getOrigenNombre());
        tramo.setDestinoNombre(dto.getDestinoNombre());
        tramo.setOrigenLatitud(dto.getOrigenLatitud());
        tramo.setOrigenLongitud(dto.getOrigenLongitud());
        tramo.setDestinoLatitud(dto.getDestinoLatitud());
        tramo.setDestinoLongitud(dto.getDestinoLongitud());
        tramo.setDistanciaEstimadaKm(dto.getDistanciaEstimadaKm());
        tramo.setDuracionEstimadaHoras(dto.getDuracionEstimadaHoras());
        tramo.setCostoEstimado(dto.getCostoEstimado());
        tramo.setCostoReal(dto.getCostoReal());
        tramo.setCamionAsignadoId(dto.getCamionAsignadoId());
        tramo.setFechaEstimadaInicio(dto.getFechaEstimadaInicio());
        tramo.setFechaEstimadaFin(dto.getFechaEstimadaFin());
        tramo.setFechaRealInicio(dto.getFechaRealInicio());
        tramo.setFechaRealFin(dto.getFechaRealFin());
        return tramo;
    }

    private TipoTramo parseTipo(String tipo) {
        if (tipo == null) {
            return null;
        }
        return TipoTramo.valueOf(tipo.toUpperCase(Locale.ROOT));
    }

    private EstadoTramo parseEstado(String estado) {
        if (estado == null) {
            return EstadoTramo.ESTIMADO;
        }
        return EstadoTramo.valueOf(estado.toUpperCase(Locale.ROOT));
    }
}
