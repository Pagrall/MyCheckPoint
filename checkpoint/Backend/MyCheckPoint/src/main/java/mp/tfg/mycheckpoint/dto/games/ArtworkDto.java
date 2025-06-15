package mp.tfg.mycheckpoint.dto.games;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO que representa una obra de arte (artwork) asociada a un juego,
 * como ilustraciones o arte conceptual.
 */
@Schema(description = "DTO para una obra de arte (artwork) de un juego.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkDto {
    /**
     * ID único de la obra de arte, generalmente proveniente de una fuente externa como IGDB.
     */
    @Schema(description = "ID de la obra de arte desde la fuente externa (ej. IGDB).", example = "2345")
    @JsonProperty("id")
    private Long igdbId;

    /**
     * URL donde se puede encontrar la imagen de la obra de arte.
     */
    @Schema(description = "URL de la imagen de la obra de arte.", example = "https://images.igdb.com/igdb/image/upload/t_original/ar2jw.jpg")
    private String url;
}