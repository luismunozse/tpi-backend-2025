package ar.edu.utn.frc.backend.tpi.orders.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class LocationsClient {

    private final RestTemplate restTemplate;
    private final ServiceClientHelper helper;

    public double calcularDistancia(Double origenLat, Double origenLon, Double destinoLat, Double destinoLon) {
        String url = helper.buildLocationsUrl("/api/v1/distancias/calcular");
        DistanceRequest request = DistanceRequest.builder()
                .origen(new Coordenada(origenLat, origenLon))
                .destino(new Coordenada(destinoLat, destinoLon))
                .build();
        ResponseEntity<DistanceResponse> response = restTemplate.postForEntity(url, request, DistanceResponse.class);
        DistanceResponse body = response.getBody();
        if (body == null || body.getDistanciaEnKilometros() == null) {
            throw new IllegalStateException("No se pudo obtener la distancia desde Locations Service");
        }
        return body.getDistanciaEnKilometros();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistanceRequest {
        private Coordenada origen;
        private Coordenada destino;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coordenada {
        private Double latitud;
        private Double longitud;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistanceResponse {
        private Double distanciaEnKilometros;
    }
}
