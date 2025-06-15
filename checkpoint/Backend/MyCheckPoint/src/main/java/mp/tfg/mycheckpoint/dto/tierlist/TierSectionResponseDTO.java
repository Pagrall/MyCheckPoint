package mp.tfg.mycheckpoint.dto.tierlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que representa una sección (tier) dentro de una Tier List en una respuesta.
 * Incluye el ID interno, nombre, orden, si es la sección por defecto para ítems sin clasificar,
 * y la lista de ítems (juegos) que contiene.
 */
@Schema(description = "DTO para una sección (tier) de una Tier List, incluyendo sus ítems.") // Schema a nivel de clase
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierSectionResponseDTO {

    /**
     * ID interno único de la sección.
     */
    @Schema(description = "ID interno de la sección.", example = "10") // Schema para el campo
    @JsonProperty("internal_id")
    private Long internalId;

    /**
     * Nombre de la sección (ej. "S Tier", "A Tier", "Juegos por Clasificar").
     */
    @Schema(description = "Nombre de la sección.", example = "S Tier") // Schema para el campo
    @JsonProperty("name")
    private String name;

    /**
     * Color de la sección en formato hexadecimal (ej. "#FF5733").
     * Este campo es opcional y puede ser nulo si no se especifica un color.
     */
    @Schema(description = "Color de la sección en formato hexadecimal.", example = "#FF5733", nullable = true)
    @JsonProperty("color")
    private String color;

    /**
     * Orden de esta sección dentro de la Tier List.
     * La sección "Juegos por Clasificar" suele tener orden 0.
     */
    @Schema(description = "Orden de la sección dentro de la Tier List.", example = "1") // Schema para el campo
    @JsonProperty("order")
    private int order;

    /**
     * Indica si esta sección es la designada por defecto para los ítems
     * que aún no han sido clasificados por el usuario.
     */
    @Schema(description = "Indica si es la sección por defecto para ítems sin clasificar.", example = "false") // Schema para el campo
    @JsonProperty("is_default_unclassified")
    private boolean isDefaultUnclassified;

    /**
     * Lista de ítems (juegos) contenidos en esta sección, representados por {@link TierListItemGameInfoDTO}.
     * Los ítems están ordenados según su posición en la sección.
     */
    @Schema(description = "Lista de ítems (juegos) en esta sección.") // Schema para el campo
    @JsonProperty("items")
    private List<TierListItemGameInfoDTO> items;
}