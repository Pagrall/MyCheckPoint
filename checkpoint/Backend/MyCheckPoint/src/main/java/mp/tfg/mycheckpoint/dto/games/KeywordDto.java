package mp.tfg.mycheckpoint.dto.games;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO que representa una palabra clave (keyword) asociada a un juego,
 * utilizada para categorización o búsqueda.
 */
@Schema(description = "DTO para una palabra clave asociada a un juego.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeywordDto {
    /**
     * ID único de la palabra clave, generalmente proveniente de una fuente externa como IGDB.
     */
    @Schema(description = "ID de la palabra clave desde la fuente externa (ej. IGDB).", example = "102")
    @JsonProperty("id")
    private Long igdbId;

    /**
     * Nombre de la palabra clave.
     */
    @Schema(description = "Nombre de la palabra clave.", example = "open world")
    private String name;
}