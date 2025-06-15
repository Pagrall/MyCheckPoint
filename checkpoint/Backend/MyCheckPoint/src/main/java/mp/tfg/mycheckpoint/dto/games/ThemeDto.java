package mp.tfg.mycheckpoint.dto.games;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO que representa un tema asociado a un videojuego (ej. Ciencia Ficción, Fantasía).
 */
@Schema(description = "DTO para un tema asociado a un videojuego.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeDto {
    /**
     * ID único del tema, generalmente proveniente de IGDB.
     */
    @Schema(description = "ID del tema desde IGDB.", example = "1")
    @JsonProperty("id")
    private Long igdbId;

    /**
     * Nombre del tema.
     */
    @Schema(description = "Nombre del tema.", example = "Science fiction")
    private String name;
}
