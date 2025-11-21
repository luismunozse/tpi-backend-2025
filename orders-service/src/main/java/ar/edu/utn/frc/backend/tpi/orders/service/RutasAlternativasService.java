package ar.edu.utn.frc.backend.tpi.orders.service;

import ar.edu.utn.frc.backend.tpi.orders.client.LocationsClient;
import ar.edu.utn.frc.backend.tpi.orders.client.PricingClient;
import ar.edu.utn.frc.backend.tpi.orders.client.FleetClient;
import ar.edu.utn.frc.backend.tpi.orders.dto.CalcularRutasAlternativasRequestDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.RutaAlternativaDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.RutasAlternativasResponseDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.TramoAlternativaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RutasAlternativasService {

    private final LocationsClient locationsClient;
    private final PricingClient pricingClient;
    private final FleetClient fleetClient;

    private static final int MAX_DEPOSITOS_POR_RUTA = 2;
    private static final int MAX_ALTERNATIVAS = 5;

    public RutasAlternativasResponseDto calcularRutasAlternativas(CalcularRutasAlternativasRequestDto request) {
        log.info("Calculando rutas alternativas desde ({}, {}) hasta ({}, {})",
                request.getLatitudOrigen(), request.getLongitudOrigen(),
                request.getLatitudDestino(), request.getLongitudDestino());

        List<RutaAlternativaDto> alternativas = new ArrayList<>();

        // 1. Ruta Directa (sin depósitos)
        RutaAlternativaDto rutaDirecta = generarRutaDirecta(request);
        if (rutaDirecta != null) {
            alternativas.add(rutaDirecta);
        }

        // 2. Obtener todos los depósitos disponibles
        LocationsClient.DepositoDto[] depositos = locationsClient.obtenerTodosLosDepositos();
        if (depositos != null && depositos.length > 0) {
            // 3. Rutas con 1 depósito intermedio
            List<RutaAlternativaDto> rutasConUnDeposito = generarRutasConUnDeposito(request, depositos);
            alternativas.addAll(rutasConUnDeposito);

            // 4. Rutas con 2 depósitos intermedios (opcional, solo las mejores)
            if (depositos.length > 1) {
                List<RutaAlternativaDto> rutasConDosDepositos = generarRutasConDosDepositos(request, depositos);
                alternativas.addAll(rutasConDosDepositos);
            }
        }

        // 5. Ordenar por costo (menor a mayor)
        alternativas.sort(Comparator.comparing(RutaAlternativaDto::getCostoEstimado));

        // 6. Limitar a MAX_ALTERNATIVAS
        if (alternativas.size() > MAX_ALTERNATIVAS) {
            alternativas = alternativas.subList(0, MAX_ALTERNATIVAS);
        }

        // 7. Marcar la ruta recomendada (la de menor costo)
        if (!alternativas.isEmpty()) {
            alternativas.get(0).setRecomendada(true);
        }

        String origen = request.getNombreOrigen() != null ? request.getNombreOrigen() : "Origen";
        String destino = request.getNombreDestino() != null ? request.getNombreDestino() : "Destino";

        return RutasAlternativasResponseDto.builder()
                .origen(origen)
                .destino(destino)
                .alternativas(alternativas)
                .totalAlternativas(alternativas.size())
                .build();
    }

    private RutaAlternativaDto generarRutaDirecta(CalcularRutasAlternativasRequestDto request) {
        try {
            double distancia = locationsClient.calcularDistancia(
                    request.getLatitudOrigen(),
                    request.getLongitudOrigen(),
                    request.getLatitudDestino(),
                    request.getLongitudDestino()
            );

            // Estimar duración (asumiendo 60 km/h promedio)
            double duracionHoras = distancia / 60.0;

            TramoAlternativaDto tramo = TramoAlternativaDto.builder()
                    .orden(1)
                    .tipo("ORIGEN_DESTINO")
                    .origenNombre(request.getNombreOrigen() != null ? request.getNombreOrigen() : "Origen")
                    .destinoNombre(request.getNombreDestino() != null ? request.getNombreDestino() : "Destino")
                    .origenLatitud(request.getLatitudOrigen())
                    .origenLongitud(request.getLongitudOrigen())
                    .destinoLatitud(request.getLatitudDestino())
                    .destinoLongitud(request.getLongitudDestino())
                    .distanciaKm(distancia)
                    .duracionHoras(duracionHoras)
                    .build();

            double costo = calcularCosto(request, List.of(tramo), 0);

            return RutaAlternativaDto.builder()
                    .id(1)
                    .descripcion("Ruta Directa")
                    .tramos(List.of(tramo))
                    .distanciaTotalKm(distancia)
                    .tiempoEstimadoHoras(duracionHoras)
                    .costoEstimado(costo)
                    .recomendada(false)
                    .cantidadDepositos(0)
                    .build();
        } catch (Exception e) {
            log.error("Error generando ruta directa", e);
            return null;
        }
    }

    private List<RutaAlternativaDto> generarRutasConUnDeposito(
            CalcularRutasAlternativasRequestDto request,
            LocationsClient.DepositoDto[] depositos) {

        List<RutaAlternativaDto> rutas = new ArrayList<>();
        int rutaId = 2;

        for (LocationsClient.DepositoDto deposito : depositos) {
            if (deposito.getCoordenada() == null) {
                continue;
            }

            try {
                // Tramo 1: Origen -> Depósito
                double distancia1 = locationsClient.calcularDistancia(
                        request.getLatitudOrigen(),
                        request.getLongitudOrigen(),
                        deposito.getCoordenada().getLatitud(),
                        deposito.getCoordenada().getLongitud()
                );
                double duracion1 = distancia1 / 60.0;

                TramoAlternativaDto tramo1 = TramoAlternativaDto.builder()
                        .orden(1)
                        .tipo("ORIGEN_DEPOSITO")
                        .origenNombre(request.getNombreOrigen() != null ? request.getNombreOrigen() : "Origen")
                        .destinoNombre(deposito.getNombre())
                        .origenLatitud(request.getLatitudOrigen())
                        .origenLongitud(request.getLongitudOrigen())
                        .destinoLatitud(deposito.getCoordenada().getLatitud())
                        .destinoLongitud(deposito.getCoordenada().getLongitud())
                        .distanciaKm(distancia1)
                        .duracionHoras(duracion1)
                        .depositoId(deposito.getId())
                        .build();

                // Tramo 2: Depósito -> Destino
                double distancia2 = locationsClient.calcularDistancia(
                        deposito.getCoordenada().getLatitud(),
                        deposito.getCoordenada().getLongitud(),
                        request.getLatitudDestino(),
                        request.getLongitudDestino()
                );
                double duracion2 = distancia2 / 60.0;

                TramoAlternativaDto tramo2 = TramoAlternativaDto.builder()
                        .orden(2)
                        .tipo("DEPOSITO_DESTINO")
                        .origenNombre(deposito.getNombre())
                        .destinoNombre(request.getNombreDestino() != null ? request.getNombreDestino() : "Destino")
                        .origenLatitud(deposito.getCoordenada().getLatitud())
                        .origenLongitud(deposito.getCoordenada().getLongitud())
                        .destinoLatitud(request.getLatitudDestino())
                        .destinoLongitud(request.getLongitudDestino())
                        .distanciaKm(distancia2)
                        .duracionHoras(duracion2)
                        .build();

                List<TramoAlternativaDto> tramos = List.of(tramo1, tramo2);
                double distanciaTotal = distancia1 + distancia2;
                double duracionTotal = duracion1 + duracion2;
                double costo = calcularCosto(request, tramos, 1);

                RutaAlternativaDto ruta = RutaAlternativaDto.builder()
                        .id(rutaId++)
                        .descripcion("Vía " + deposito.getNombre())
                        .tramos(tramos)
                        .distanciaTotalKm(distanciaTotal)
                        .tiempoEstimadoHoras(duracionTotal)
                        .costoEstimado(costo)
                        .recomendada(false)
                        .cantidadDepositos(1)
                        .build();

                rutas.add(ruta);
            } catch (Exception e) {
                log.warn("Error generando ruta con depósito {}: {}", deposito.getNombre(), e.getMessage());
            }
        }

        return rutas;
    }

    private List<RutaAlternativaDto> generarRutasConDosDepositos(
            CalcularRutasAlternativasRequestDto request,
            LocationsClient.DepositoDto[] depositos) {

        List<RutaAlternativaDto> rutas = new ArrayList<>();
        int rutaId = 100;

        // Solo generar combinaciones de 2 depósitos para los primeros N depósitos
        int maxDepositos = Math.min(depositos.length, 3);

        for (int i = 0; i < maxDepositos; i++) {
            LocationsClient.DepositoDto dep1 = depositos[i];
            if (dep1.getCoordenada() == null) continue;

            for (int j = i + 1; j < maxDepositos; j++) {
                LocationsClient.DepositoDto dep2 = depositos[j];
                if (dep2.getCoordenada() == null) continue;

                try {
                    // Tramo 1: Origen -> Depósito 1
                    double dist1 = locationsClient.calcularDistancia(
                            request.getLatitudOrigen(),
                            request.getLongitudOrigen(),
                            dep1.getCoordenada().getLatitud(),
                            dep1.getCoordenada().getLongitud()
                    );
                    double dur1 = dist1 / 60.0;

                    TramoAlternativaDto tramo1 = TramoAlternativaDto.builder()
                            .orden(1)
                            .tipo("ORIGEN_DEPOSITO")
                            .origenNombre(request.getNombreOrigen() != null ? request.getNombreOrigen() : "Origen")
                            .destinoNombre(dep1.getNombre())
                            .origenLatitud(request.getLatitudOrigen())
                            .origenLongitud(request.getLongitudOrigen())
                            .destinoLatitud(dep1.getCoordenada().getLatitud())
                            .destinoLongitud(dep1.getCoordenada().getLongitud())
                            .distanciaKm(dist1)
                            .duracionHoras(dur1)
                            .depositoId(dep1.getId())
                            .build();

                    // Tramo 2: Depósito 1 -> Depósito 2
                    double dist2 = locationsClient.calcularDistancia(
                            dep1.getCoordenada().getLatitud(),
                            dep1.getCoordenada().getLongitud(),
                            dep2.getCoordenada().getLatitud(),
                            dep2.getCoordenada().getLongitud()
                    );
                    double dur2 = dist2 / 60.0;

                    TramoAlternativaDto tramo2 = TramoAlternativaDto.builder()
                            .orden(2)
                            .tipo("DEPOSITO_DEPOSITO")
                            .origenNombre(dep1.getNombre())
                            .destinoNombre(dep2.getNombre())
                            .origenLatitud(dep1.getCoordenada().getLatitud())
                            .origenLongitud(dep1.getCoordenada().getLongitud())
                            .destinoLatitud(dep2.getCoordenada().getLatitud())
                            .destinoLongitud(dep2.getCoordenada().getLongitud())
                            .distanciaKm(dist2)
                            .duracionHoras(dur2)
                            .depositoId(dep2.getId())
                            .build();

                    // Tramo 3: Depósito 2 -> Destino
                    double dist3 = locationsClient.calcularDistancia(
                            dep2.getCoordenada().getLatitud(),
                            dep2.getCoordenada().getLongitud(),
                            request.getLatitudDestino(),
                            request.getLongitudDestino()
                    );
                    double dur3 = dist3 / 60.0;

                    TramoAlternativaDto tramo3 = TramoAlternativaDto.builder()
                            .orden(3)
                            .tipo("DEPOSITO_DESTINO")
                            .origenNombre(dep2.getNombre())
                            .destinoNombre(request.getNombreDestino() != null ? request.getNombreDestino() : "Destino")
                            .origenLatitud(dep2.getCoordenada().getLatitud())
                            .origenLongitud(dep2.getCoordenada().getLongitud())
                            .destinoLatitud(request.getLatitudDestino())
                            .destinoLongitud(request.getLongitudDestino())
                            .distanciaKm(dist3)
                            .duracionHoras(dur3)
                            .build();

                    List<TramoAlternativaDto> tramos = List.of(tramo1, tramo2, tramo3);
                    double distanciaTotal = dist1 + dist2 + dist3;
                    double duracionTotal = dur1 + dur2 + dur3;
                    double costo = calcularCosto(request, tramos, 2);

                    RutaAlternativaDto ruta = RutaAlternativaDto.builder()
                            .id(rutaId++)
                            .descripcion("Vía " + dep1.getNombre() + " y " + dep2.getNombre())
                            .tramos(tramos)
                            .distanciaTotalKm(distanciaTotal)
                            .tiempoEstimadoHoras(duracionTotal)
                            .costoEstimado(costo)
                            .recomendada(false)
                            .cantidadDepositos(2)
                            .build();

                    rutas.add(ruta);
                } catch (Exception e) {
                    log.warn("Error generando ruta con depósitos {} y {}: {}",
                            dep1.getNombre(), dep2.getNombre(), e.getMessage());
                }
            }
        }

        return rutas;
    }

    private double calcularCosto(CalcularRutasAlternativasRequestDto request,
                                  List<TramoAlternativaDto> tramos,
                                  int cantidadDepositos) {
        try {
            double distanciaTotal = tramos.stream()
                    .mapToDouble(TramoAlternativaDto::getDistanciaKm)
                    .sum();

            // Obtener consumo promedio de camiones elegibles
            double consumoPromedio = 0.35;
            FleetClient.CamionDto[] camionesElegibles = fleetClient.obtenerCamionesDisponibles(
                    request.getPesoContenedorKg(),
                    request.getVolumenContenedorM3()
            );

            if (camionesElegibles != null && camionesElegibles.length > 0) {
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
                    distanciaTotal,
                    distanciaTotal,
                    request.getPesoContenedorKg(),
                    request.getVolumenContenedorM3(),
                    consumoPromedio,
                    costoCombustible,
                    tramos.size(),
                    cantidadDepositos,
                    List.of()
            );

            PricingClient.CalculoTarifaResponse response = pricingClient.calcularTarifa(pricingRequest);
            if (response != null && response.costoTotal() != null) {
                return response.costoTotal();
            }
        } catch (Exception e) {
            log.warn("Error calculando costo: {}", e.getMessage());
        }

        // Costo estimado simple si falla el cálculo detallado
        double distanciaTotal = tramos.stream()
                .mapToDouble(TramoAlternativaDto::getDistanciaKm)
                .sum();
        return distanciaTotal * 100; // $100 por km como estimación básica
    }
}
