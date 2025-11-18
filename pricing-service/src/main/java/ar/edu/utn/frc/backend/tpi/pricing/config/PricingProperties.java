package ar.edu.utn.frc.backend.tpi.pricing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pricing.combustible")
@Data
public class PricingProperties {

    private Double costoLitro;
}

