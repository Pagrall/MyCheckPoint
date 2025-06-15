package mp.tfg.mycheckpoint.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar un cambio de contraseña.
 * Requiere la contraseña actual del usuario para verificación y la nueva contraseña deseada.
 */
@Schema(description = "DTO para solicitar un cambio de contraseña. Requiere la contraseña actual del usuario y la nueva contraseña deseada.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeDTO {

    /**
     * La contraseña actual del usuario para verificación.
     */
    @Schema(description = "La contraseña actual del usuario para verificación.",
            example = "ContraseñaActual123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La contraseña actual no puede estar vacía.")
    @JsonProperty("contraseña_actual")
    private String contraseñaActual;

    /**
     * La nueva contraseña deseada para la cuenta.
     * Debe tener entre 8 y 100 caracteres.
     */
    @Schema(description = "La nueva contraseña deseada para la cuenta. Debe tener entre 8 y 100 caracteres.",
            example = "NuevaContraseñaS3gur@", requiredMode = Schema.RequiredMode.REQUIRED, format = "password", minLength = 8, maxLength = 100)
    @NotBlank(message = "La nueva contraseña no puede estar vacía.")
    @Size(min = 8, max = 100, message = "La nueva contraseña debe tener entre 8 y 100 caracteres.")
    @JsonProperty("nueva_contraseña")
    private String nuevaContraseña;

}
