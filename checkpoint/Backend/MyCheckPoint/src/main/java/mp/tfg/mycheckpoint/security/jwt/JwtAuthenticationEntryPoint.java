package mp.tfg.mycheckpoint.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Punto de entrada para la autenticación JWT.
 * Esta clase se invoca cuando un usuario no autenticado intenta acceder a un recurso protegido.
 * En lugar de redirigir a una página de login (comportamiento común en aplicaciones web tradicionales),
 * para una API REST, devuelve una respuesta HTTP 401 Unauthorized.
 */
@Component // Anotado como Component para que Spring lo gestione y pueda ser inyectado.
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    /**
     * Este método se llama siempre que se lanza una excepción debido a un principal no autenticado
     * que intenta acceder a un recurso que requiere autenticación.
     *
     * @param request La solicitud que resultó en una {@link AuthenticationException}.
     * @param response La respuesta para que el implementador pueda modificarla.
     * @param authException La excepción que causó la invocación.
     * @throws IOException Si ocurre un error de entrada/salida al enviar el error.
     * @throws ServletException Si ocurre un error de servlet.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Respondiendo con error no autorizado. Mensaje - {}", authException.getMessage());
        // Devuelve un error HTTP 401 Unauthorized.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: No autorizado");
    }
}