package mp.tfg.mycheckpoint.dto.games;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO que representa una captura de pantalla (screenshot) de un juego.
 */
@Schema(description = "DTO para una captura de pantalla de un juego.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenshotDto {
    /**
     * ID único de la captura de pantalla, generalmente proveniente de una fuente externa como IGDB.
     */
    @Schema(description = "ID de la captura de pantalla desde la fuente externa (ej. IGDB).", example = "3456")
    @JsonProperty("id")
    private Long igdbId;

    /**
     * URL donde se puede encontrar la imagen de la captura de pantalla.
     */
    @Schema(description = "URL de la imagen de la captura de pantalla.", example = "//images.igdb.com/igdb/image/upload/t_original/sc6x8q.jpg")
    private String url;
}
