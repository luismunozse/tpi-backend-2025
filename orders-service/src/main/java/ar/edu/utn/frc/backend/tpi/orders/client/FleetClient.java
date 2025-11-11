package ar.edu.utn.frc.backend.tpi.orders.client;

import ar.edu.utn.frc.backend.tpi.orders.client.ServiceClientHelper;
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

    public void actualizarEstadoCamion(Long camionId, String estado) {
        String url = helper.buildFleetUrl("/api/v1/camiones/" + camionId + "/estado");
        RestTemplatePatchRequest request = new RestTemplatePatchRequest(estado);
        restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(request), Void.class);
    }

    public record CamionDto(Long id, Double capacidadPesoKg, Double capacidadVolumenM3, String estado) {}

    public record RestTemplatePatchRequest(String estado) {}
}
