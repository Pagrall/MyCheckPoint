package mp.tfg.mycheckpoint.dto.games;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO que contiene información resumida sobre un juego similar.
 * Utilizado para listar juegos que podrían ser de interés para el usuario.
 */
@Schema(description = "DTO con información resumida de un juego similar.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimilarGameInfoDto {
    /**
     * ID único del juego similar, generalmente proveniente de IGDB.
     */
    @Schema(description = "ID del juego similar desde IGDB.", example = "19560")
    @JsonProperty("id")
    private Long igdbId;

    /**
     * Información de la carátula del juego similar.
     */
    @Schema(description = "Carátula del juego similar.")
    private CoverDto cover;

    /**
     * Nombre del juego similar.
     */
    @Schema(description = "Nombre del juego similar.", example = "The Elder Scrolls V: Skyrim")
    private String name;

    /**
     * Identificador URL amigable (slug) del juego similar.
     */
    @Schema(description = "Slug del juego similar.", example = "the-elder-scrolls-v-skyrim")
    private String slug;

    /**
     * Resumen breve del juego similar. Puede ser nulo.
     */
    @Schema(description = "Resumen del juego similar.", example = "An open-world action role-playing video game.", nullable = true)
    private String summary;

    /**
     * Calificación total del juego similar. Puede ser nulo.
     */
    @Schema(description = "Calificación total del juego similar.", example = "96.0", nullable = true)
    @JsonProperty("total_rating")
    private Double totalRating;

}
