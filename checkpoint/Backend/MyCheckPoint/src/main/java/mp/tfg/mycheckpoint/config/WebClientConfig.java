package mp.tfg.mycheckpoint.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración para crear y gestionar instancias de {@link WebClient}.
 * Actualmente, define un WebClient específico para interactuar con la API de IGDB.
 */
@Configuration
public class WebClientConfig {

    /**
     * URL base de la API de IGDB.
     * Inyectada desde las propiedades de la aplicación (ej. application.properties).
     */
    @Value("${igdb.api.baseurl}")
    private String igdbBaseUrl;

    /**
     * Client-ID para la autenticación con la API de IGDB.
     * Inyectada desde las propiedades de la aplicación.
     */
    @Value("${igdb.api.client-id}")
    private String igdbClientId;

    /**
     * Cabecera de autorización completa (incluyendo "Bearer ") para la API de IGDB.
     * Inyectada desde las propiedades de la aplicación.
     */
    @Value("${igdb.api.authorization}")
    private String igdbAuthorizationHeader;

    /**
     * Define un bean {@link WebClient} configurado para interactuar con la API de IGDB.
     * Establece la URL base, y cabeceras por defecto como {@code Accept} para JSON,
     * {@code Client-ID}, y {@code Authorization}.
     *
     * @param builder El constructor de WebClient proporcionado por Spring.
     * @return Una instancia de {@link WebClient} configurada para IGDB.
     */
    @Bean
    public WebClient igdbWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(igdbBaseUrl) // Establece la URL base para todas las solicitudes de este cliente
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE) // Cabecera Accept por defecto
                .defaultHeader("Client-ID", igdbClientId) // Cabecera Client-ID para IGDB
                .defaultHeader(HttpHeaders.AUTHORIZATION, igdbAuthorizationHeader) // Cabecera de Autorización para IGDB
                .build();
    }
}
