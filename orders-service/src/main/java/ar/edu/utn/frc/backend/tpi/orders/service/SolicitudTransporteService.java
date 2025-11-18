package ar.edu.utn.frc.backend.tpi.orders.service;

import ar.edu.utn.frc.backend.tpi.orders.client.FleetClient;
import ar.edu.utn.frc.backend.tpi.orders.client.LocationsClient;
import ar.edu.utn.frc.backend.tpi.orders.client.PricingClient;
import ar.edu.utn.frc.backend.tpi.orders.dto.AsignarCamionRequestDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.CalculoSolicitudResponseDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.CrearSolicitudRequestDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.SolicitudTransporteDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.TramoDto;
import ar.edu.utn.frc.backend.tpi.orders.exception.NegocioException;
import ar.edu.utn.frc.backend.tpi.orders.exception.RecursoNoEncontradoException;
import ar.edu.utn.frc.backend.tpi.orders.mapper.RutaMapper;
import ar.edu.utn.frc.backend.tpi.orders.mapper.SolicitudTransporteMapper;
import ar.edu.utn.frc.backend.tpi.orders.mapper.TramoMapper;
import ar.edu.utn.frc.backend.tpi.orders.model.Cliente;
import ar.edu.utn.frc.backend.tpi.orders.model.Contenedor;
import ar.edu.utn.frc.backend.tpi.orders.model.EstadoContenedor;
import ar.edu.utn.frc.backend.tpi.orders.model.EstadoSolicitud;
import ar.edu.utn.frc.backend.tpi.orders.model.EstadoTramo;
import ar.edu.utn.frc.backend.tpi.orders.model.Ruta;
import ar.edu.utn.frc.backend.tpi.orders.model.SolicitudTransporte;
import ar.edu.utn.frc.backend.tpi.orders.model.TipoTramo;
import ar.edu.utn.frc.backend.tpi.orders.model.Tramo;
import ar.edu.utn.frc.backend.tpi.orders.repository.ClienteRepository;
import ar.edu.utn.frc.backend.tpi.orders.repository.ContenedorRepository;
import ar.edu.utn.frc.backend.tpi.orders.repository.SolicitudTransporteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SolicitudTransporteService {

    private final SolicitudTransporteRepository solicitudRepository;
    private final ClienteRepository clienteRepository;
    private final ContenedorRepository contenedorRepository;
    private final SolicitudTransporteMapper solicitudMapper;
    private final TramoMapper tramoMapper;
    private final NumeroSolicitudGenerator numeroSolicitudGenerator;
    private final FleetClient fleetClient;
    private final LocationsClient locationsClient;
    private final PricingClient pricingClient;

    public SolicitudTransporteDto crearSolicitud(CrearSolicitudRequestDto request) {
        Cliente cliente = obtenerOcrearCliente(request);
        Contenedor contenedor = obtenerOcrearContenedor(request, cliente);

        Ruta ruta = construirRutaDesdeRequest(request);

        SolicitudTransporte solicitud = new SolicitudTransporte();
        solicitud.setNumeroSolicitud(numeroSolicitudGenerator.generarNumero());
        solicitud.setEstado(EstadoSolicitud.BORRADOR);
        solicitud.setCliente(cliente);
        solicitud.setContenedor(contenedor);
        solicitud.setRuta(ruta);
        ruta.setSolicitud(solicitud);

        solicitud.setFechaCreacion(LocalDateTime.now());
        solicitud.setFechaActualizacion(LocalDateTime.now());

        calcularCostosEstimados(solicitud, request.getTramos());

        SolicitudTransporte guardada = solicitudRepository.save(solicitud);
        return solicitudMapper.toDto(guardada);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public SolicitudTransporteDto obtenerPorId(Long id) {
        SolicitudTransporte solicitud = solicitudRepository.findDetailedById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada con id " + id));
        return solicitudMapper.toDto(solicitud);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<SolicitudTransporteDto> listar(Optional<String> estado) {
        if (estado.isPresent()) {
            EstadoSolicitud estadoSolicitud = EstadoSolicitud.valueOf(estado.get().toUpperCase(Locale.ROOT));
            return solicitudRepository.findByEstadoOrderByFechaCreacionDesc(estadoSolicitud)
                    .stream().map(solicitudMapper::toDto).collect(Collectors.toList());
        }
        return solicitudRepository.findAll().stream()
                .sorted(Comparator.comparing(SolicitudTransporte::getFechaCreacion).reversed())
                .map(solicitudMapper::toDto)
                .collect(Collectors.toList());
    }

    public SolicitudTransporteDto actualizarEstado(Long id, String nuevoEstado) {
        SolicitudTransporte solicitud = solicitudRepository.findDetailedById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada con id " + id));

        EstadoSolicitud estadoActual = solicitud.getEstado();
        EstadoSolicitud estadoDestino = EstadoSolicitud.valueOf(nuevoEstado.toUpperCase(Locale.ROOT));
        validarTransicionEstado(estadoActual, estadoDestino, solicitud);

        solicitud.setEstado(estadoDestino);
        solicitud.setFechaActualizacion(LocalDateTime.now());

        if (estadoDestino == EstadoSolicitud.PROGRAMADA) {
            solicitud.getContenedor().setEstado(EstadoContenedor.LISTO_PARA_RETIRO);
        } else if (estadoDestino == EstadoSolicitud.EN_TRANSITO) {
            solicitud.getContenedor().setEstado(EstadoContenedor.EN_TRASLADO);
        } else if (estadoDestino == EstadoSolicitud.ENTREGADA) {
            solicitud.getContenedor().setEstado(EstadoContenedor.ENTREGADO);
            solicitud.setTiempoRealHoras(calcularTiempoReal(solicitud));
            solicitud.setCostoFinal(solicitud.getCostoEstimado());
        } else if (estadoDestino == EstadoSolicitud.CANCELADA) {
            solicitud.getContenedor().setEstado(EstadoContenedor.REGISTRADO);
        }

        return solicitudMapper.toDto(solicitud);
    }

    public TramoDto asignarCamion(Long solicitudId, Long tramoId, AsignarCamionRequestDto request) {
        SolicitudTransporte solicitud = solicitudRepository.findDetailedById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada con id " + solicitudId));

        Tramo tramo = solicitud.getRuta().getTramos().stream()
                .filter(t -> t.getId().equals(tramoId))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("Tramo no encontrado con id " + tramoId));

        if (tramo.getEstado() != EstadoTramo.ESTIMADO && tramo.getEstado() != EstadoTramo.ASIGNADO) {
            throw new NegocioException("No se puede asignar un camión a un tramo en estado " + tramo.getEstado());
        }

        FleetClient.CamionDto camion = fleetClient.obtenerCamionPorId(request.getCamionId());
        if (camion == null) {
            throw new RecursoNoEncontradoException("Camión no encontrado con id " + request.getCamionId());
        }

        Contenedor contenedor = solicitud.getContenedor();
        if (camion.capacidadPesoKg() != null && contenedor.getPesoKg() != null
                && contenedor.getPesoKg() > camion.capacidadPesoKg()) {
            throw new NegocioException("El camión no soporta el peso del contenedor");
        }
        if (camion.capacidadVolumenM3() != null && contenedor.getVolumenM3() != null
                && contenedor.getVolumenM3() > camion.capacidadVolumenM3()) {
            throw new NegocioException("El camión no soporta el volumen del contenedor");
        }

        tramo.setCamionAsignadoId(request.getCamionId());
        tramo.setEstado(EstadoTramo.ASIGNADO);
        solicitud.setFechaActualizacion(LocalDateTime.now());

        fleetClient.actualizarEstadoCamion(request.getCamionId(), "OCUPADO");

        return tramoMapper.toDto(tramo);
    }

    public TramoDto registrarInicioTramo(Long solicitudId, Long tramoId) {
        SolicitudTransporte solicitud = solicitudRepository.findDetailedById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada con id " + solicitudId));

        Tramo tramo = encontrarTramo(solicitud, tramoId);
        if (tramo.getEstado() != EstadoTramo.ASIGNADO && tramo.getEstado() != EstadoTramo.ESTIMADO) {
            throw new NegocioException("El tramo no está listo para iniciar");
        }

        tramo.setEstado(EstadoTramo.INICIADO);
        tramo.setFechaRealInicio(LocalDateTime.now());
        solicitud.setEstado(EstadoSolicitud.EN_TRANSITO);
        solicitud.getContenedor().setEstado(EstadoContenedor.EN_TRASLADO);
        solicitud.setFechaActualizacion(LocalDateTime.now());

        return tramoMapper.toDto(tramo);
    }

    public TramoDto registrarFinTramo(Long solicitudId, Long tramoId, Double costoReal) {
        SolicitudTransporte solicitud = solicitudRepository.findDetailedById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada con id " + solicitudId));

        Tramo tramo = encontrarTramo(solicitud, tramoId);
        if (tramo.getEstado() != EstadoTramo.INICIADO) {
            throw new NegocioException("El tramo no está en ejecución");
        }

        tramo.setEstado(EstadoTramo.FINALIZADO);
        tramo.setFechaRealFin(LocalDateTime.now());
        if (costoReal != null) {
            tramo.setCostoReal(costoReal);
        } else {
            tramo.setCostoReal(tramo.getCostoEstimado());
        }

        if (tramo.getCamionAsignadoId() != null) {
            fleetClient.actualizarEstadoCamion(tramo.getCamionAsignadoId(), "DISPONIBLE");
        }

        if (todosTramosFinalizados(solicitud.getRuta().getTramos())) {
            solicitud.setEstado(EstadoSolicitud.ENTREGADA);
            solicitud.getContenedor().setEstado(EstadoContenedor.ENTREGADO);
            solicitud.setTiempoRealHoras(calcularTiempoReal(solicitud));
            solicitud.setCostoFinal(solicitud.getRuta().getTramos().stream()
                    .map(Tramo::getCostoReal)
                    .filter(java.util.Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .sum());
        }

        solicitud.setFechaActualizacion(LocalDateTime.now());
        return tramoMapper.toDto(tramo);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TramoDto> obtenerTracking(Long solicitudId) {
        SolicitudTransporte solicitud = solicitudRepository.findDetailedById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada con id " + solicitudId));

        return solicitud.getRuta().getTramos().stream()
                .sorted(Comparator.comparing(Tramo::getOrden))
                .map(tramoMapper::toDto)
                .collect(Collectors.toList());
    }

    private Cliente obtenerOcrearCliente(CrearSolicitudRequestDto request) {
        return clienteRepository.findByEmailIgnoreCase(request.getEmailCliente())
                .map(cliente -> {
                    cliente.setNombre(request.getNombreCliente());
                    cliente.setTelefono(request.getTelefonoCliente());
                    return cliente;
                })
                .orElseGet(() -> {
                    Cliente nuevo = new Cliente();
                    nuevo.setNombre(request.getNombreCliente());
                    nuevo.setEmail(request.getEmailCliente());
                    nuevo.setTelefono(request.getTelefonoCliente());
                    return nuevo;
                });
    }

    private Contenedor obtenerOcrearContenedor(CrearSolicitudRequestDto request, Cliente cliente) {
        return contenedorRepository.findByIdentificadorIgnoreCase(request.getIdentificadorContenedor())
                .map(contenedor -> {
                    if (contenedor.getEstado() == EstadoContenedor.EN_TRASLADO) {
                        throw new NegocioException("El contenedor ya está en traslado");
                    }
                    contenedor.setPesoKg(request.getPesoContenedorKg());
                    contenedor.setVolumenM3(request.getVolumenContenedorM3());
                    contenedor.setEstado(EstadoContenedor.REGISTRADO);
                    contenedor.setCliente(cliente);
                    return contenedor;
                })
                .orElseGet(() -> {
                    Contenedor nuevo = new Contenedor();
                    nuevo.setIdentificador(request.getIdentificadorContenedor());
                    nuevo.setPesoKg(request.getPesoContenedorKg());
                    nuevo.setVolumenM3(request.getVolumenContenedorM3());
                    nuevo.setEstado(EstadoContenedor.REGISTRADO);
                    nuevo.setCliente(cliente);
                    return nuevo;
                });
    }

    private Ruta construirRutaDesdeRequest(CrearSolicitudRequestDto request) {
        Ruta ruta = new Ruta();
        List<Tramo> tramos = new ArrayList<>();
        double distanciaTotal = 0.0;
        double duracionTotal = 0.0;
        for (CrearSolicitudRequestDto.TramoRequest tramoRequest : request.getTramos()) {
            Tramo tramo = new Tramo();
            tramo.setOrden(tramoRequest.getOrden());
            tramo.setTipo(TipoTramo.valueOf(tramoRequest.getTipo().toUpperCase(Locale.ROOT)));
            tramo.setEstado(EstadoTramo.ESTIMADO);
            tramo.setOrigenNombre(tramoRequest.getOrigenNombre());
            tramo.setDestinoNombre(tramoRequest.getDestinoNombre());
            tramo.setOrigenLatitud(tramoRequest.getOrigenLatitud());
            tramo.setOrigenLongitud(tramoRequest.getOrigenLongitud());
            tramo.setDestinoLatitud(tramoRequest.getDestinoLatitud());
            tramo.setDestinoLongitud(tramoRequest.getDestinoLongitud());

            double distanciaTramo = tramoRequest.getDistanciaEstimadaKm();
            if ((tramoRequest.getOrigenLatitud() != null && tramoRequest.getOrigenLongitud() != null
                    && tramoRequest.getDestinoLatitud() != null && tramoRequest.getDestinoLongitud() != null)) {
                distanciaTramo = locationsClient.calcularDistancia(
                        tramoRequest.getOrigenLatitud(),
                        tramoRequest.getOrigenLongitud(),
                        tramoRequest.getDestinoLatitud(),
                        tramoRequest.getDestinoLongitud());
            }
            tramo.setDistanciaEstimadaKm(distanciaTramo);
            tramo.setDuracionEstimadaHoras(tramoRequest.getDuracionEstimadaHoras());
            tramo.setRuta(ruta);
            tramos.add(tramo);
            distanciaTotal += distanciaTramo;
            duracionTotal += tramoRequest.getDuracionEstimadaHoras();
        }
        tramos.sort(Comparator.comparing(Tramo::getOrden));
        ruta.setTramos(tramos);
        ruta.setDistanciaTotalKm(distanciaTotal);
        ruta.setDuracionTotalHoras(duracionTotal);
        return ruta;
    }

    private void calcularCostosEstimados(SolicitudTransporte solicitud, List<CrearSolicitudRequestDto.TramoRequest> tramosRequest) {
        Ruta ruta = solicitud.getRuta();
        double distanciaTotalKm = ruta.getDistanciaTotalKm();
        double consumoPromedio = 0.35;
        Double pesoContenedor = solicitud.getContenedor() != null ? solicitud.getContenedor().getPesoKg() : null;
        Double volumenContenedor = solicitud.getContenedor() != null ? solicitud.getContenedor().getVolumenM3() : null;

        if (pesoContenedor != null && volumenContenedor != null) {
            FleetClient.CamionDto[] camionesElegibles = fleetClient.obtenerCamionesDisponibles(pesoContenedor, volumenContenedor);
            double sumaConsumo = 0.0;
            int cantidadConDato = 0;
            for (FleetClient.CamionDto camion : camionesElegibles) {
                if (camion.consumoCombustiblePorKm() != null) {
                    sumaConsumo += camion.consumoCombustiblePorKm();
                    cantidadConDato++;
                }
            }
            if (cantidadConDato > 0) {
                consumoPromedio = sumaConsumo / cantidadConDato;
            }
        }

        double costoCombustible = 1.0;

        PricingClient.CalculoTarifaRequest pricingRequest = new PricingClient.CalculoTarifaRequest(
                distanciaTotalKm,
                distanciaTotalKm,
                solicitud.getContenedor().getPesoKg(),
                solicitud.getContenedor().getVolumenM3(),
                consumoPromedio,
                costoCombustible,
                tramosRequest.size(),
                0,
                List.of()
        );

        PricingClient.CalculoTarifaResponse response = pricingClient.calcularTarifa(pricingRequest);
        if (response != null) {
            solicitud.setCostoEstimado(response.costoTotal());
            ruta.getTramos().forEach(tramo -> {
                double proporcion = tramo.getDistanciaEstimadaKm() / (distanciaTotalKm == 0 ? 1 : distanciaTotalKm);
                tramo.setCostoEstimado(response.costoTotal() * proporcion);
            });
        } else {
            solicitud.setCostoEstimado(0.0);
        }
        solicitud.setTiempoEstimadoHoras(ruta.getDuracionTotalHoras());
    }

    private void validarTransicionEstado(EstadoSolicitud actual, EstadoSolicitud destino, SolicitudTransporte solicitud) {
        if (actual == destino) {
            return;
        }
        switch (actual) {
            case BORRADOR -> {
                if (destino != EstadoSolicitud.PROGRAMADA && destino != EstadoSolicitud.CANCELADA) {
                    throw new NegocioException("No se puede pasar de BORRADOR a " + destino);
                }
            }
            case PROGRAMADA -> {
                if (destino != EstadoSolicitud.EN_TRANSITO && destino != EstadoSolicitud.CANCELADA) {
                    throw new NegocioException("No se puede pasar de PROGRAMADA a " + destino);
                }
                boolean algunAsignado = solicitud.getRuta() != null && solicitud.getRuta().getTramos().stream()
                        .anyMatch(tramo -> tramo.getCamionAsignadoId() != null);
                if (destino == EstadoSolicitud.EN_TRANSITO && !algunAsignado) {
                    throw new NegocioException("No hay camiones asignados a los tramos");
                }
            }
            case EN_TRANSITO -> {
                if (destino != EstadoSolicitud.ENTREGADA && destino != EstadoSolicitud.CANCELADA) {
                    throw new NegocioException("No se puede pasar de EN_TRANSITO a " + destino);
                }
                if (destino == EstadoSolicitud.ENTREGADA && !todosTramosFinalizados(solicitud.getRuta().getTramos())) {
                    throw new NegocioException("No todos los tramos se finalizaron");
                }
            }
            case ENTREGADA, CANCELADA -> throw new NegocioException("La solicitud ya está " + actual);
        }
    }

    private boolean todosTramosFinalizados(List<Tramo> tramos) {
        return tramos != null && tramos.stream().allMatch(tramo -> tramo.getEstado() == EstadoTramo.FINALIZADO);
    }

    private Tramo encontrarTramo(SolicitudTransporte solicitud, Long tramoId) {
        return solicitud.getRuta().getTramos().stream()
                .filter(t -> t.getId().equals(tramoId))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("Tramo no encontrado con id " + tramoId));
    }

    private Double calcularTiempoReal(SolicitudTransporte solicitud) {
        if (solicitud.getRuta() == null) {
            return null;
        }
        long minutos = solicitud.getRuta().getTramos().stream()
                .filter(tramo -> tramo.getFechaRealInicio() != null && tramo.getFechaRealFin() != null)
                .map(tramo -> Duration.between(tramo.getFechaRealInicio(), tramo.getFechaRealFin()).toMinutes())
                .reduce(Long::sum)
                .orElse(0L);
        return minutos == 0 ? null : minutos / 60.0;
    }

    public CalculoSolicitudResponseDto recalcularCosto(Long solicitudId) {
        SolicitudTransporte solicitud = solicitudRepository.findDetailedById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada con id " + solicitudId));

        Ruta ruta = solicitud.getRuta();
        if (ruta == null) {
            throw new NegocioException("La solicitud no tiene ruta asociada");
        }

        PricingClient.CalculoTarifaRequest pricingRequest = new PricingClient.CalculoTarifaRequest(
                ruta.getDistanciaTotalKm(),
                ruta.getDistanciaTotalKm(),
                solicitud.getContenedor().getPesoKg(),
                solicitud.getContenedor().getVolumenM3(),
                0.35,
                1.0,
                ruta.getTramos().size(),
                0,
                List.of()
        );

        PricingClient.CalculoTarifaResponse response = pricingClient.calcularTarifa(pricingRequest);
        if (response == null) {
            throw new NegocioException("No se pudo recalcular el costo");
        }

        solicitud.setCostoEstimado(response.costoTotal());
        solicitud.setFechaActualizacion(LocalDateTime.now());

        List<CalculoSolicitudResponseDto.DetalleRecargoDto> detalle = response.detalleRecargos() == null ? List.of()
                : response.detalleRecargos().stream()
                .map(recargo -> CalculoSolicitudResponseDto.DetalleRecargoDto.builder()
                        .id(recargo.id())
                        .descripcion(recargo.descripcion())
                        .porcentaje(recargo.porcentaje())
                        .montoFijo(recargo.montoFijo())
                        .montoAplicado(recargo.montoAplicado())
                        .build())
                .collect(Collectors.toList());

        return CalculoSolicitudResponseDto.builder()
                .costoBaseKm(response.costoBaseKm())
                .costoCombustible(response.costoCombustible())
                .costosDeposito(response.costosDeposito())
                .costosGestion(response.costosGestion())
                .recargos(response.recargos())
                .costoTotal(response.costoTotal())
                .tiempoEstimadoHoras(ruta.getDuracionTotalHoras())
                .distanciaTotalKm(ruta.getDistanciaTotalKm())
                .recargosDetallados(detalle)
                .build();
    }
}
