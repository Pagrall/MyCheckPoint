package mp.tfg.mycheckpoint.dto.gameList;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para crear o actualizar una lista de juegos personalizada.
 * Contiene el nombre, una descripción opcional y el estado de visibilidad.
 */
@Schema(description = "DTO para crear o actualizar una lista de juegos personalizada.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameListRequestDTO {

    /**
     * Nombre de la lista de juegos.
     * Debe tener entre 1 y 150 caracteres.
     */
    @Schema(description = "Nombre de la lista de juegos. Debe tener entre 1 y 150 caracteres.",
            example = "Mis Juegos Favoritos de RPG", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 150)
    @NotBlank(message = "List name cannot be blank")
    @Size(min = 1, max = 150, message = "List name must be between 1 and 150 characters")
    @JsonProperty("name")
    private String name;

    /**
     * Descripción opcional para la lista de juegos.
     * No puede exceder los 1000 caracteres.
     */
    @Schema(description = "Descripción opcional para la lista de juegos. Máximo 1000 caracteres.",
            example = "Una colección de los RPGs que más he disfrutado.", maxLength = 1000, nullable = true)
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @JsonProperty("description")
    private String description;

    /**
     * Indica si la lista de juegos es pública (true) o privada (false).
     * Este campo es obligatorio.
     */
    @Schema(description = "Indica si la lista de juegos es pública (true) o privada (false).",
            example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Public status cannot be null")
    @JsonProperty("is_public")
    private Boolean isPublic;
}
