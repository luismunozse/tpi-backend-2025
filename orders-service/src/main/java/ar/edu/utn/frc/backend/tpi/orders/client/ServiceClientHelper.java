package ar.edu.utn.frc.backend.tpi.orders.client;

import ar.edu.utn.frc.backend.tpi.orders.config.ServiceEndpointsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceClientHelper {

    private final ServiceEndpointsProperties properties;

    public String buildFleetUrl(String path) {
        return buildUrl(properties.getFleetBaseUrl(), path);
    }

    public String buildLocationsUrl(String path) {
        return buildUrl(properties.getLocationsBaseUrl(), path);
    }

    public String buildPricingUrl(String path) {
        return buildUrl(properties.getPricingBaseUrl(), path);
    }

    private String buildUrl(String base, String path) {
        if (base == null) {
            throw new IllegalStateException("La URL base del servicio no está configurada");
        }
        if (path == null || path.isEmpty()) {
            return base;
        }
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return base + path;
    }
}
