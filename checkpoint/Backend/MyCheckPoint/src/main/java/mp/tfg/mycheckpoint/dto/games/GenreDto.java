package mp.tfg.mycheckpoint.dto.games;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO que representa un género de videojuego.
 */
@Schema(description = "DTO para un género de videojuego.")
@Data
public class GenreDto {
    /**
     * ID único del género, generalmente proveniente de IGDB.
     */
    @Schema(description = "ID del género desde IGDB.", example = "12")
    @JsonProperty("id")
    private Long igdbId;

    /**
     * Nombre del género.
     */
    @Schema(description = "Nombre del género.", example = "Role-playing (RPG)")
    private String name;
}