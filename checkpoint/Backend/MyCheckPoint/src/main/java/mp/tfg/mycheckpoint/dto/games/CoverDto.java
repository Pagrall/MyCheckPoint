package mp.tfg.mycheckpoint.dto.games;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO que representa la información de la carátula (cover) de un juego.
 */
@Schema(description = "DTO para la información de la carátula de un juego.") // Añadido Schema a nivel de clase
@Data
public class CoverDto {
    /**
     * ID único de la carátula, generalmente proveniente de una fuente externa como IGDB.
     */
    @Schema(description = "ID de la carátula desde la fuente externa (ej. IGDB).", example = "85210") // Añadido Schema
    @JsonProperty("id") // Mapea el campo "id" del JSON a este atributo
    private Long igdbId;

    /**
     * URL donde se puede encontrar la imagen de la carátula.
     */
    @Schema(description = "URL de la imagen de la carátula.", example = "//images.igdb.com/igdb/image/upload/t_cover_big/co1vja.jpg") // Añadido Schema
    private String url;
}