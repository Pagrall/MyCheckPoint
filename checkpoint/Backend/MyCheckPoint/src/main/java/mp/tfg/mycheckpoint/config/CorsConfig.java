package mp.tfg.mycheckpoint.config;// Ejemplo de configuración global de CORS en Spring Boot
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS (Cross-Origin Resource Sharing) para la aplicación Spring Boot.
 * Permite que las solicitudes desde orígenes específicos (como un frontend desarrollado en Vite)
 * accedan a los endpoints de la API bajo "/api/v1/**".
 */
@Configuration
public class CorsConfig {

    /**
     * Define un bean {@link WebMvcConfigurer} para personalizar la configuración de CORS.
     * Configura mapeos CORS para permitir solicitudes desde el frontend de Vite
     * (http://localhost:5173) a todos los endpoints bajo "/api/v1/**",
     * especificando los métodos HTTP, cabeceras permitidas y si se permiten credenciales.
     *
     * @return Una instancia de {@link WebMvcConfigurer} con la configuración CORS.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/v1/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);

                registry.addMapping("/api/juegos/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
