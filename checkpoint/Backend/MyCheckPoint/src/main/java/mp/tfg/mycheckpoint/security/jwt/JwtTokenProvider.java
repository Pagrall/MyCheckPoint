package mp.tfg.mycheckpoint.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import mp.tfg.mycheckpoint.security.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Proveedor de utilidades para la generación y validación de tokens JWT (JSON Web Tokens).
 * Utiliza una clave secreta y un tiempo de expiración configurables desde las propiedades de la aplicación.
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    /**
     * Clave secreta utilizada para firmar y verificar los tokens JWT.
     * Debe ser una cadena codificada en Base64 lo suficientemente segura.
     * Se inyecta desde la propiedad "app.jwt.secret".
     */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /**
     * Tiempo de expiración para los tokens JWT, en milisegundos.
     * Se inyecta desde la propiedad "app.jwt.expiration-ms".
     */
    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    /**
     * Obtiene la clave de firma ({@link SecretKey}) a partir de la cadena {@code jwtSecret} codificada en Base64.
     * @return La clave secreta para firmar y verificar tokens.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token JWT para un usuario autenticado.
     * El "subject" del token será el nombre de usuario (email en este caso) del principal.
     *
     * @param authentication El objeto {@link Authentication} de Spring Security que contiene los detalles del usuario.
     * @return Una cadena que representa el token JWT generado.
     */
    public String generateToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(userPrincipal.getUsername()) // El "subject" del token es el email del usuario
                .issuedAt(now) // Fecha de emisión
                .expiration(expiryDate) // Fecha de expiración
                .signWith(getSigningKey(), Jwts.SIG.HS512) // Firma con la clave secreta y algoritmo HS512
                .compact();
    }

    /**
     * Genera un token JWT a partir de un nombre de usuario (o identificador, como el email).
     * Útil para casos donde se necesita generar un token sin un objeto {@link Authentication} completo,
     * por ejemplo, después de un registro exitoso si se desea devolver un token inmediatamente.
     *
     * @param username El nombre de usuario (o email) para el cual generar el token.
     * @return Una cadena que representa el token JWT generado.
     */
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Extrae el nombre de usuario (el "subject" del token, que es el email en esta implementación)
     * de un token JWT dado.
     *
     * @param token El token JWT del cual extraer el nombre de usuario.
     * @return El nombre de usuario (email) contenido en el token.
     * @throws io.jsonwebtoken.JwtException Si el token no puede ser parseado o verificado.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey()) // Verifica la firma con la clave secreta
                .build()
                .parseSignedClaims(token) // Parsea el token firmado
                .getPayload(); // Obtiene el cuerpo (claims) del token
        return claims.getSubject();
    }

    /**
     * Valida un token JWT.
     * Comprueba la firma, si está malformado, si ha expirado, si no es soportado o si los claims están vacíos.
     *
     * @param authToken El token JWT a validar.
     * @return {@code true} si el token es válido, {@code false} en caso contrario.
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Firma del token JWT inválida: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT inválido (malformado): {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT no soportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            // Esta excepción puede ocurrir si el token es nulo, vacío o solo espacios en blanco,
            // o si los claims son inválidos de alguna otra manera.
            logger.error("Claims del token JWT vacías o argumento ilegal: {}", ex.getMessage());
        }
        return false;
    }
}