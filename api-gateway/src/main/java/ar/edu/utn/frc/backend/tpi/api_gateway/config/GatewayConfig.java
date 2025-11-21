package ar.edu.utn.frc.backend.tpi.api_gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

@Configuration
public class GatewayConfig {

    private static final Logger log = LoggerFactory.getLogger(GatewayConfig.class);

    @Value("${TPI_ORDERS_SERVICE_URL:http://localhost:8083}")
    private String ordersServiceUrl;

    @Value("${TPI_FLEET_SERVICE_URL:http://localhost:8084}")
    private String fleetServiceUrl;

    @Value("${TPI_LOCATIONS_SERVICE_URL:http://localhost:8085}")
    private String locationsServiceUrl;

    @Value("${TPI_PRICING_SERVICE_URL:http://localhost:8086}")
    private String pricingServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> ordersServiceRoute() {
        log.info("Configurando ruta para Orders Service: /api/ordenes/** -> {}", ordersServiceUrl);
        return route("orders-service")
                .route(RequestPredicates.path("/api/ordenes/**"), HandlerFunctions.http(ordersServiceUrl))
                .filter(rewritePath("/api/ordenes(?<segment>/?.*)", "/api/v1${segment}"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fleetServiceRoute() {
        log.info("Configurando ruta para Fleet Service: /api/fleet/** -> {}", fleetServiceUrl);
        return route("fleet-service")
                .route(RequestPredicates.path("/api/fleet/**"), HandlerFunctions.http(fleetServiceUrl))
                .filter(rewritePath("/api/fleet(?<segment>/?.*)", "/api/v1${segment}"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> locationsServiceRoute() {
        log.info("Configurando ruta para Locations Service: /api/locations/** -> {}", locationsServiceUrl);
        return route("locations-service")
                .route(RequestPredicates.path("/api/locations/**"), HandlerFunctions.http(locationsServiceUrl))
                .filter(rewritePath("/api/locations(?<segment>/?.*)", "/api/v1${segment}"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> pricingServiceRoute() {
        log.info("Configurando ruta para Pricing Service: /api/pricing/** -> {}", pricingServiceUrl);
        return route("pricing-service")
                .route(RequestPredicates.path("/api/pricing/**"), HandlerFunctions.http(pricingServiceUrl))
                .filter(rewritePath("/api/pricing(?<segment>/?.*)", "/api/v1${segment}"))
                .build();
    }
}
