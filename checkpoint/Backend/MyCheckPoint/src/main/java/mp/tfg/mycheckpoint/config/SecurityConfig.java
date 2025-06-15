package mp.tfg.mycheckpoint.config;


import mp.tfg.mycheckpoint.security.jwt.JwtAuthenticationEntryPoint;
import mp.tfg.mycheckpoint.security.jwt.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad principal para la aplicación utilizando Spring Security.
 * Habilita la seguridad web, la seguridad a nivel de método y configura la
 * cadena de filtros de seguridad, el manejo de JWT, y las reglas de autorización de endpoints.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita la seguridad a nivel de método (ej. @PreAuthorize)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructor para SecurityConfig.
     *
     * @param unauthorizedHandler Manejador para puntos de entrada de autenticación no autorizada.
     * @param jwtAuthenticationFilter Filtro para procesar tokens JWT en las solicitudes.
     */
    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Define el codificador de contraseñas que se utilizará en la aplicación.
     * @return Una instancia de {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expone el {@link AuthenticationManager} de Spring Security como un bean.
     *
     * @param authenticationConfiguration La configuración de autenticación de Spring.
     * @return El AuthenticationManager configurado.
     * @throws Exception Si ocurre un error al obtener el AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configura la cadena de filtros de seguridad ({@link SecurityFilterChain}).
     * Define las reglas de autorización para los diferentes endpoints de la API,
     * deshabilita CSRF, configura el manejo de excepciones de autenticación,
     * establece la política de creación de sesiones como STATELESS (adecuado para APIs REST con JWT),
     * y añade el filtro de autenticación JWT.
     *
     * @param http El objeto {@link HttpSecurity} para configurar la seguridad.
     * @return La cadena de filtros de seguridad construida.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF, común para APIs REST
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Manejador para accesos no autorizados
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sesiones sin estado para JWT
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos (autenticación, registro, swagger, etc.)
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios").permitAll() // Registro de usuario
                        .requestMatchers(HttpMethod.GET, "/api/v1/usuarios/public/**").permitAll() // Perfiles públicos de usuario
                        .requestMatchers(HttpMethod.GET, "/api/v1/usuarios/public/summary/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll() // Documentación Swagger/OpenAPI
                        .requestMatchers(HttpMethod.GET,"/api/v1/auth/confirm-account").permitAll() // Confirmación de cuenta
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/forgot-password").permitAll() // Olvido de contraseña
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/reset-password").permitAll() // Reseteo de contraseña
                        .requestMatchers(HttpMethod.GET, "/api/juegos/igdb/filtrar").permitAll() // Filtrado público de juegos IGDB
                        .requestMatchers(HttpMethod.GET, "/api/v1/games/{igdbId}/details").permitAll() // Vista detallada pública de juego
                        .requestMatchers(HttpMethod.GET, "/api/juegos/**").permitAll() // Búsqueda y listado público de juegos
                        .requestMatchers(HttpMethod.GET, "/api/v1/gamelists/public").permitAll() // Ver todas las GameLists públicas
                        .requestMatchers(HttpMethod.GET, "/api/v1/gamelists/{listPublicId}/public").permitAll() // Ver una GameList pública
                        .requestMatchers("/profile-pictures/**").permitAll() // Servir imágenes de perfil estáticas

                        // Endpoints de Biblioteca de Juegos del Usuario (requieren autenticación)
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/me/library/games/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me/library/games/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/me/library/games/**").authenticated()

                        // Endpoints de Listas de Juegos del Usuario (requieren autenticación)
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/me/gamelists/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me/gamelists/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/me/gamelists/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/me/gamelists/**").authenticated()

                        // Endpoints de Amistad (requieren autenticación)
                        .requestMatchers("/api/v1/friends/**").authenticated()

                        // Endpoints de Usuario (búsqueda, subida de foto de perfil, etc. - requieren autenticación)
                        .requestMatchers(HttpMethod.GET, "/api/v1/usuarios/search").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios/me/profile-picture").authenticated()
                        // NO PROBADO .requestMatchers(HttpMethod.POST, "/api/juegos/igdb/sincronizar/**").authenticated() // Sincronizar juego desde IGDB

                        // --- Endpoints para Tier Lists ---
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/me/tierlists").authenticated() // Crear TierList de perfil
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me/tierlists").authenticated() // Obtener TierLists de perfil del usuario actual
                        .requestMatchers(HttpMethod.GET, "/api/v1/gamelists/{gameListPublicId}/tierlist").permitAll() // Obtener/crear TierList para GameList (servicio valida acceso)
                        .requestMatchers(HttpMethod.GET, "/api/v1/tierlists/public").permitAll() // Listar todas las TierLists públicas
                        .requestMatchers(HttpMethod.GET, "/api/v1/tierlists/{tierListPublicId}").permitAll() // Obtener TierList específica (servicio valida acceso)
                        // Modificaciones de TierList (requieren autenticación y ser propietario, validado en servicio)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/tierlists/{tierListPublicId}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tierlists/{tierListPublicId}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/tierlists/{tierListPublicId}/sections").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/tierlists/{tierListPublicId}/sections/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tierlists/{tierListPublicId}/sections/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/tierlists/{tierListPublicId}/items/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/tierlists/{tierListPublicId}/items/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tierlists/{tierListPublicId}/items/**").authenticated()

                        // Cualquier otra solicitud no definida explícitamente requiere autenticación.
                        .anyRequest().authenticated()
                );

        // Añade el filtro de autenticación JWT antes del filtro estándar de UsernamePasswordAuthenticationFilter.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}