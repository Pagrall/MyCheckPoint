package mp.tfg.mycheckpoint.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de restablecimiento de contraseña.
 * Contiene el token de restablecimiento recibido por correo electrónico y la nueva contraseña deseada.
 */
@Schema(description = "DTO para la solicitud de restablecimiento de contraseña. Contiene el token recibido por email y la nueva contraseña.")
@Data
@NoArgsConstructor
public class ResetPasswordDTO {

    /**
     * El token de restablecimiento único que el usuario recibió por correo electrónico.
     */
    @Schema(description = "El token de restablecimiento único que el usuario recibió por correo electrónico.",
            example = "a1b2c3d4-e5f6-7890-1234-567890abcdef", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El token de reseteo no puede estar vacío.")
    @JsonProperty("token")
    private String token;

    /**
     * La nueva contraseña deseada para la cuenta.
     * Debe tener entre 8 y 100 caracteres.
     */
    @Schema(description = "La nueva contraseña deseada para la cuenta. Debe tener entre 8 y 100 caracteres.",
            example = "NuevaContraseñaS3gur@!", requiredMode = Schema.RequiredMode.REQUIRED, format = "password", minLength = 8, maxLength = 100)
    @NotBlank(message = "La nueva contraseña no puede estar vacía.")
    @Size(min = 8, max = 100, message = "La nueva contraseña debe tener entre 8 y 100 caracteres.")
    @JsonProperty("nueva_contraseña")
    private String nuevaContraseña;

}
