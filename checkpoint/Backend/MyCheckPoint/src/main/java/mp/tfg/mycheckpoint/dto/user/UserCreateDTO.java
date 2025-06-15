package mp.tfg.mycheckpoint.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la creación de un nuevo usuario.
 * Contiene los campos obligatorios para el registro: nombre de usuario, email y contraseña.
 */
@Schema(description = "DTO para la creación de un nuevo usuario. Contiene los campos obligatorios para el registro.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

    /**
     * Nombre de usuario único para la cuenta.
     * Debe tener entre 3 y 100 caracteres.
     */
    @Schema(description = "Nombre de usuario único para la cuenta. Debe tener entre 3 y 100 caracteres.",
            example = "nuevoUsuario123", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 3, maxLength = 100)
    @JsonProperty("nombre_usuario")
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre de usuario debe tener entre 3 y 100 caracteres")
    private String nombreUsuario;

    /**
     * Dirección de correo electrónico del usuario.
     * Debe ser única y tener un formato válido. No puede exceder los 255 caracteres.
     */
    @Schema(description = "Dirección de correo electrónico del usuario. Debe ser única y tener un formato válido.",
            example = "usuario@example.com", requiredMode = Schema.RequiredMode.REQUIRED, format = "email", maxLength = 255)
    @JsonProperty("email")
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    @Size(max = 255, message = "El email no puede exceder los 255 caracteres")
    private String email;

    /**
     * Contraseña para la cuenta del usuario.
     * Debe tener entre 8 y 100 caracteres.
     */
    @Schema(description = "Contraseña para la cuenta del usuario. Debe tener entre 8 y 100 caracteres.",
            example = "ContraseñaS3gur@", requiredMode = Schema.RequiredMode.REQUIRED, format = "password", minLength = 8, maxLength = 100)
    @JsonProperty("contraseña")
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    private String contraseña;


}