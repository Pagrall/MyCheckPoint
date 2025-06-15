package mp.tfg.mycheckpoint.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mp.tfg.mycheckpoint.security.UserDetailsServiceImpl; // Necesitamos nuestro UserDetailsService
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component; // Hacerlo un componente para poder inyectar en SecurityConfig
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT que se ejecuta una vez por cada solicitud.
 * Este filtro intercepta las solicitudes HTTP, extrae el token JWT de la cabecera
 * "Authorization", valida el token y, si es válido, establece la autenticación
 * del usuario en el contexto de seguridad de Spring ({@link SecurityContextHolder}).
 */
@Component // Anotado como Component para que Spring lo gestione y pueda ser inyectado en SecurityConfig.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Procesa cada solicitud HTTP para verificar y establecer la autenticación JWT.
     *
     * @param request La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @param filterChain La cadena de filtros a la que se pasará la solicitud.
     * @throws ServletException Si ocurre un error de servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // El nombre de usuario en el token JWT es el email del usuario.
                String username = tokenProvider.getUsernameFromToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails != null && userDetails.isEnabled()) { // Verifica también si la cuenta está habilitada
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    // logger.debug("Usuario {} autenticado correctamente mediante JWT.", username); // Opcional: Log de éxito
                } else if (userDetails != null && !userDetails.isEnabled()){
                    logger.warn("Intento de autenticación JWT para usuario {} fallido: la cuenta está deshabilitada.", username);
                }
            }
        } catch (Exception ex) {
            logger.error("No se pudo establecer la autenticación del usuario en el contexto de seguridad: {}", ex.getMessage());
            // No se relanza la excepción aquí para permitir que otros filtros actúen
            // o que la cadena de seguridad maneje la falta de autenticación más adelante.
            // El JwtAuthenticationEntryPoint se encargará de enviar un 401 si es necesario.
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT de la cabecera "Authorization" de la solicitud HTTP.
     * El token debe tener el prefijo "Bearer ".
     *
     * @param request La solicitud HTTP.
     * @return El token JWT como una cadena, o {@code null} si no se encuentra o no tiene el formato correcto.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Extrae el token después de "Bearer "
        }
        return null;
    }
}