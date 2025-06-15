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
 * DTO para la solicitud de creación o actualización del nombre de una sección (tier)
 * dentro de una Tier List.
 */
@Schema(description = "DTO para solicitar la creación o actualización del nombre de una nueva sección (tier) dentro de una Tier List.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierSectionRequestDTO {

    /**
     * Nombre de la sección (tier).
     * Debe tener entre 1 y 100 caracteres. Es obligatorio.
     */
    @Schema(description = "Nombre de la nueva sección (tier). Debe tener entre 1 y 100 caracteres.",
            example = "S Tier", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 100)
    @NotBlank(message = "El nombre de la sección no puede estar vacío.")
    @Size(min = 1, max = 100, message = "El nombre de la sección debe tener entre 1 y 100 caracteres.")
    @JsonProperty("name")
    private String name;

    @Schema(description = "Color de la sección (tier) en formato hexadecimal.",
            example = "#FF5733", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El color de la sección no puede estar vacío.")
    @Size(min = 1, max = 7, message = "El color de la sección debe tener entre 1 y 7 caracteres (formato hexadecimal).")
    @JsonProperty("color")
    private String color;
}