package ar.edu.utn.frc.backend.tpi.api_gateway.config;

import ar.edu.utn.frc.backend.tpi.api_gateway.interceptor.UserContextPropagationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Spring MVC para el API Gateway.
 * Registra interceptores que se ejecutan antes de procesar las peticiones.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserContextPropagationInterceptor userContextInterceptor;

    public WebMvcConfig(UserContextPropagationInterceptor userContextInterceptor) {
        this.userContextInterceptor = userContextInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Aplicar el interceptor a todas las rutas de API
        // Excluir endpoints públicos de autenticación y actuator
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/auth/**", "/actuator/**");
    }
}
