package tn.itbs.tp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    // CORS configuration is now handled in application.properties
    // to avoid duplicate headers

    /*
     * @Bean
     * public CorsWebFilter corsWebFilter() {
     * CorsConfiguration corsConfig = new CorsConfiguration();
     * 
     * // Allow credentials
     * corsConfig.setAllowCredentials(true);
     * 
     * // Allow Angular frontend
     * corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
     * 
     * // Allow all headers
     * corsConfig.addAllowedHeader("*");
     * 
     * // Allow all HTTP methods
     * corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE",
     * "OPTIONS", "PATCH"));
     * 
     * UrlBasedCorsConfigurationSource source = new
     * UrlBasedCorsConfigurationSource();
     * source.registerCorsConfiguration("/**", corsConfig);
     * 
     * return new CorsWebFilter(source);
     * }
     */
}
