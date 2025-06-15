package mp.tfg.mycheckpoint.dto.games;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO que representa un sitio web asociado a un juego (ej. sitio oficial, página de Steam).
 */
@Schema(description = "DTO para un sitio web asociado a un juego.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebsiteDto {
    /**
     * ID único del sitio web, generalmente proveniente de una fuente externa como IGDB.
     */
    @Schema(description = "ID del sitio web desde la fuente externa (ej. IGDB).", example = "5678")
    @JsonProperty("id")
    private Long igdbId;

    /**
     * URL completa del sitio web.
     */
    @Schema(description = "URL completa del sitio web.", example = "https://www.nintendo.com/store/products/the-legend-of-zelda-breath-of-the-wild-switch/")
    private String url;
}
