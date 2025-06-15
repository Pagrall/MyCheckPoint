package mp.tfg.mycheckpoint.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de una autenticación exitosa.
 * Contiene el token de acceso JWT y el tipo de token.
 */
@Schema(description = "DTO para la respuesta de inicio de sesión exitoso. Contiene el token de acceso JWT.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {

    /**
     * Token de acceso JWT generado para el usuario autenticado.
     */
    @Schema(description = "Token de acceso JWT generado para el usuario autenticado.",
            example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c3VhcmlvQGV4YW1wbGUuY29tIiwiaWF0IjoxNj...")
    @JsonProperty("token_acceso")
    private String accessToken;

    /**
     * Tipo de token, generalmente "Bearer".
     */
    @Schema(description = "Tipo de token, generalmente 'Bearer'.", example = "Bearer")
    @JsonProperty("tipo_token")
    private String tokenType = "Bearer"; // Estándar para tokens Bearer


    /**
     * Constructor que solo toma el token de acceso.
     * El tipo de token se establece por defecto a "Bearer".
     * @param accessToken El token de acceso JWT.
     */
    public JwtResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}