package mp.tfg.mycheckpoint.dto.games;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import mp.tfg.mycheckpoint.dto.enums.GameType;

/**
 * DTO que contiene información resumida sobre un DLC (Contenido Descargable) o una expansión.
 * Utilizado para representar juegos relacionados como DLCs, expansiones, o juegos padre/versión.
 */
@Schema(description = "DTO con información resumida de un DLC, expansión o juego relacionado.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DlcInfoDto {
    /**
     * ID único del DLC o juego relacionado, generalmente proveniente de IGDB.
     */
    @Schema(description = "ID del DLC/expansión/juego relacionado desde IGDB.", example = "129340")
    @JsonProperty("id")
    private Long igdbId;

    /**
     * Información de la carátula del DLC o juego relacionado.
     */
    @Schema(description = "Carátula del DLC/expansión/juego relacionado.")
    private CoverDto cover;

    /**
     * Nombre del DLC o juego relacionado.
     */
    @Schema(description = "Nombre del DLC/expansión/juego relacionado.", example = "The Witcher 3: Wild Hunt - Blood and Wine")
    private String name;

    /**
     * Calificación total del DLC o juego relacionado. Puede ser nulo.
     */
    @Schema(description = "Calificación total del DLC/expansión/juego relacionado.", example = "92.0", nullable = true)
    @JsonProperty("total_rating")
    private Double totalRating;

    /**
     * Tipo de juego, útil para distinguir entre DLC, expansión, etc.
     * Generalmente será {@link GameType#DLC} o {@link GameType#EXPANSION}.
     */
    @Schema(description = "Tipo de juego (ej. DLC, EXPANSION).", example = "EXPANSION")
    @JsonProperty("game_type")
    private GameType gameType;

    /**
     * Identificador URL amigable (slug) del DLC o juego relacionado.
     */
    @Schema(description = "Slug del DLC/expansión/juego relacionado.", example = "the-witcher-3-wild-hunt-blood-and-wine")
    @JsonProperty("slug")
    private String slug;
}
