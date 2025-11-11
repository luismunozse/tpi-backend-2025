package ar.edu.utn.frc.backend.tpi.orders.mapper;

import ar.edu.utn.frc.backend.tpi.orders.dto.SolicitudTransporteDto;
import ar.edu.utn.frc.backend.tpi.orders.model.EstadoSolicitud;
import ar.edu.utn.frc.backend.tpi.orders.model.SolicitudTransporte;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class SolicitudTransporteMapper {

    private final ClienteMapper clienteMapper;
    private final ContenedorMapper contenedorMapper;
    private final RutaMapper rutaMapper;

    public SolicitudTransporteDto toDto(SolicitudTransporte solicitud) {
        if (solicitud == null) {
            return null;
        }
        return SolicitudTransporteDto.builder()
                .id(solicitud.getId())
                .numeroSolicitud(solicitud.getNumeroSolicitud())
                .estado(solicitud.getEstado() != null ? solicitud.getEstado().name() : null)
                .cliente(clienteMapper.toDto(solicitud.getCliente()))
                .contenedor(contenedorMapper.toDto(solicitud.getContenedor()))
                .ruta(rutaMapper.toDto(solicitud.getRuta()))
                .costoEstimado(solicitud.getCostoEstimado())
                .costoFinal(solicitud.getCostoFinal())
                .tiempoEstimadoHoras(solicitud.getTiempoEstimadoHoras())
                .tiempoRealHoras(solicitud.getTiempoRealHoras())
                .fechaCreacion(solicitud.getFechaCreacion())
                .fechaActualizacion(solicitud.getFechaActualizacion())
                .build();
    }

    public SolicitudTransporte toEntity(SolicitudTransporteDto dto) {
        if (dto == null) {
            return null;
        }
        SolicitudTransporte solicitud = new SolicitudTransporte();
        solicitud.setId(dto.getId());
        solicitud.setNumeroSolicitud(dto.getNumeroSolicitud());
        solicitud.setEstado(parseEstado(dto.getEstado()));
        solicitud.setCliente(clienteMapper.toEntity(dto.getCliente()));
        solicitud.setContenedor(contenedorMapper.toEntity(dto.getContenedor()));
        solicitud.setRuta(rutaMapper.toEntity(dto.getRuta()));
        solicitud.setCostoEstimado(dto.getCostoEstimado());
        solicitud.setCostoFinal(dto.getCostoFinal());
        solicitud.setTiempoEstimadoHoras(dto.getTiempoEstimadoHoras());
        solicitud.setTiempoRealHoras(dto.getTiempoRealHoras());
        solicitud.setFechaCreacion(dto.getFechaCreacion());
        solicitud.setFechaActualizacion(dto.getFechaActualizacion());
        return solicitud;
    }

    private EstadoSolicitud parseEstado(String estado) {
        if (estado == null) {
            return EstadoSolicitud.BORRADOR;
        }
        return EstadoSolicitud.valueOf(estado.toUpperCase(Locale.ROOT));
    }
}
