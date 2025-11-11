package ar.edu.utn.frc.backend.tpi.orders.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tpi.services")
public class ServiceEndpointsProperties {

    private String fleetBaseUrl;
    private String locationsBaseUrl;
    private String pricingBaseUrl;
}
