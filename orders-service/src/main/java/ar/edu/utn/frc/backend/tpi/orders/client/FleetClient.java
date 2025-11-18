package ar.edu.utn.frc.backend.tpi.orders.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class FleetClient {

    private final RestTemplate restTemplate;
    private final ServiceClientHelper helper;

    public CamionDto obtenerCamionPorId(Long camionId) {
        String url = helper.buildFleetUrl("/api/v1/camiones/" + camionId);
        ResponseEntity<CamionDto> response = restTemplate.getForEntity(url, CamionDto.class);
        return response.getBody();
    }

    public CamionDto[] obtenerCamionesDisponibles(Double peso, Double volumen) {
        String url = helper.buildFleetUrl("/api/v1/camiones/disponibles?peso=" + peso + "&volumen=" + volumen);
        ResponseEntity<CamionDto[]> response = restTemplate.getForEntity(url, CamionDto[].class);
        CamionDto[] body = response.getBody();
        return body != null ? body : new CamionDto[0];
    }

    public void actualizarEstadoCamion(Long camionId, String estado) {
        String url = helper.buildFleetUrl("/api/v1/camiones/" + camionId + "/estado");
        RestTemplatePatchRequest request = new RestTemplatePatchRequest(estado);
        restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(request), Void.class);
    }

    public record CamionDto(Long id,
                            Double capacidadPesoKg,
                            Double capacidadVolumenM3,
                            Double consumoCombustiblePorKm,
                            Double costoBasePorKm,
                            String estado) {}

    public record RestTemplatePatchRequest(String estado) {}
}
