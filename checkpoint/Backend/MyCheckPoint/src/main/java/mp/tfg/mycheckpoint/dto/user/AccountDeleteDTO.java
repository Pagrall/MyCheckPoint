package mp.tfg.mycheckpoint.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para confirmar la solicitud de eliminación de cuenta.
 * Requiere la contraseña actual del usuario para verificar su identidad.
 */
@Schema(description = "DTO utilizado para confirmar la solicitud de eliminación de cuenta. Requiere la contraseña actual del usuario.")
@Data
@NoArgsConstructor
public class AccountDeleteDTO {

    /**
     * La contraseña actual del usuario.
     * Es necesaria para verificar la identidad antes de programar la eliminación de la cuenta.
     */
    @Schema(description = "La contraseña actual del usuario. Es necesaria para verificar la identidad antes de programar la eliminación de la cuenta.",
            example = "ContraseñaActual123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La contraseña actual es requerida para eliminar la cuenta.")
    @JsonProperty("contraseña_actual")
    private String contraseñaActual;
}