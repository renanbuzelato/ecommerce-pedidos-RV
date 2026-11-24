package com.ecommerce.pedidos.RV.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Libera o front-end React (outra origem) para chamar a API.
 * Em desenvolvimento o proxy do Vite já evita CORS; isto vale para o build
 * publicado ou para quem apontar o front direto para http://localhost:8080/api.
 * Origens configuráveis em app.cors.origens (application.properties).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] origens;

    public CorsConfig(@Value("${app.cors.origens:http://localhost:5173,http://localhost:4173}") String[] origens) {
        this.origens = origens;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(origens)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    }
}
