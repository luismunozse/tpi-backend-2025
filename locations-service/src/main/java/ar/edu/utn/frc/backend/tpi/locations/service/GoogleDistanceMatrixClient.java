package ar.edu.utn.frc.backend.tpi.locations.service;

import ar.edu.utn.frc.backend.tpi.locations.model.Coordenada;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class GoogleDistanceMatrixClient {

    private final RestClient restClient;

    @Value("${google.maps.distance-matrix.base-url}")
    private String baseUrl;

    @Value("${google.maps.distance-matrix.api-key}")
    private String apiKey;

    public double obtenerDistanciaEnKm(Coordenada origen, Coordenada destino) {
        String origins = origen.getLatitud() + "," + origen.getLongitud();
        String destinations = destino.getLatitud() + "," + destino.getLongitud();

        try {
            ResponseEntity<GoogleDistanceMatrixResponse> response = restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("maps.googleapis.com")
                            .path("/maps/api/distancematrix/json")
                            .queryParam("origins", origins)
                            .queryParam("destinations", destinations)
                            .queryParam("units", "metric")
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .toEntity(GoogleDistanceMatrixResponse.class);

            GoogleDistanceMatrixResponse body = response.getBody();
            if (body == null
                    || body.getRows() == null
                    || body.getRows().isEmpty()
                    || body.getRows().get(0).getElements() == null
                    || body.getRows().get(0).getElements().isEmpty()) {
                throw new IllegalStateException("Respuesta inválida de Google Distance Matrix");
            }

            GoogleDistanceMatrixResponse.Element element =
                    body.getRows().get(0).getElements().get(0);

            if (!"OK".equalsIgnoreCase(element.getStatus())) {
                throw new IllegalStateException("Error en Distance Matrix: " + element.getStatus());
            }

            long metros = element.getDistance().getValue();
            return metros / 1000.0;
        } catch (RestClientException e) {
            throw new IllegalStateException("Error llamando a Google Distance Matrix", e);
        }
    }
}
