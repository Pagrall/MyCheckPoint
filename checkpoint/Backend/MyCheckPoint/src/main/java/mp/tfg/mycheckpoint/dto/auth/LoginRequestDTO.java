package mp.tfg.mycheckpoint.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de inicio de sesión.
 * Requiere un identificador (que puede ser el email o el nombre de usuario) y la contraseña.
 */
@Schema(description = "DTO para la solicitud de inicio de sesión. Requiere un identificador (email o nombre de usuario) y una contraseña.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    /**
     * Identificador del usuario para el inicio de sesión.
     * Puede ser la dirección de correo electrónico o el nombre de usuario.
     */
    @Schema(description = "Identificador del usuario, puede ser su email o su nombre de usuario.",
            example = "usuario123", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("identificador")
    @NotBlank(message = "El identificador (email o nombre de usuario) no puede estar vacío")
    private String identificador;

    /**
     * Contraseña del usuario para el inicio de sesión.
     */
    @Schema(description = "Contraseña del usuario.",
            example = "P@$$wOrd", requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
    @JsonProperty("contraseña")
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String contraseña;
}