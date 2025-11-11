package ar.edu.utn.frc.backend.tpi.orders.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PricingClient {

    private final RestTemplate restTemplate;
    private final ServiceClientHelper helper;

    public CalculoTarifaResponse calcularTarifa(CalculoTarifaRequest request) {
        String url = helper.buildPricingUrl("/api/v1/calculos/tarifa");
        ResponseEntity<CalculoTarifaResponse> response = restTemplate.postForEntity(url, request, CalculoTarifaResponse.class);
        return response.getBody();
    }

    public record CalculoTarifaRequest(
            Double distanciaTotalKm,
            Double distanciaRecorridaPorCamionKm,
            Double pesoContenedorKg,
            Double volumenContenedorM3,
            Double consumoCamionLtsPorKm,
            Double costoCombustiblePorLitro,
            Integer cantidadTramos,
            Integer diasTotalesEnDeposito,
            java.util.List<Long> recargosAplicados
    ) {}

    public record CalculoTarifaResponse(
            Double costoBaseKm,
            Double costoCombustible,
            Double costosDeposito,
            Double costosGestion,
            Double recargos,
            Double costoTotal,
            java.util.List<DetalleRecargo> detalleRecargos
    ) {
        public record DetalleRecargo(Long id, String descripcion, Double porcentaje, Double montoFijo, Double montoAplicado) {}
    }
}
