package mp.tfg.mycheckpoint.dto.tierlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de creación de una nueva Tier List.
 * Se utiliza principalmente para Tier Lists de perfil general del usuario.
 * Permite especificar el nombre, una descripción opcional y la visibilidad.
 */
@Schema(description = "DTO para crear una nueva Tier List. Se utiliza principalmente para Tier Lists de perfil.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierListCreateRequestDTO {

    /**
     * Nombre de la Tier List.
     * Debe tener entre 1 y 150 caracteres. Es obligatorio.
     */
    @Schema(description = "Nombre de la Tier List. Debe tener entre 1 y 150 caracteres.",
            example = "Mis Personajes Favoritos de Street Fighter", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 150)
    @NotBlank(message = "El nombre de la Tier List no puede estar vacío.")
    @Size(min = 1, max = 150, message = "El nombre de la Tier List debe tener entre 1 y 150 caracteres.")
    @JsonProperty("name")
    private String name;

    /**
     * Descripción opcional para la Tier List.
     * No puede exceder los 1000 caracteres.
     */
    @Schema(description = "Descripción opcional para la Tier List. Máximo 1000 caracteres.",
            example = "Clasificación personal de personajes basada en su jugabilidad.", maxLength = 1000, nullable = true)
    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres.")
    @JsonProperty("description")
    private String description;

    /**
     * Indica si la Tier List será pública (true) o privada (false).
     * Si no se especifica, por defecto es {@code false} (privada).
     */
    @Schema(description = "Indica si la Tier List será pública (true) o privada (false). Por defecto es false (privada).",
            example = "false", nullable = true)
    @JsonProperty("is_public")
    private Boolean isPublic = false;
}