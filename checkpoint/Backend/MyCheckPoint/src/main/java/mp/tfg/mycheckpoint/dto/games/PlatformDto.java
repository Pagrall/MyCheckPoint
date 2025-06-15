package mp.tfg.mycheckpoint.dto.games;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO que representa una plataforma de videojuegos (ej. PC, PlayStation 5).
 * Incluye información como el nombre, nombre alternativo y el logo de la plataforma.
 */
@Schema(description = "DTO para una plataforma de videojuegos.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformDto {
    /**
     * ID único de la plataforma, generalmente proveniente de IGDB.
     */
    @Schema(description = "ID de la plataforma desde IGDB.", example = "6")
    @JsonProperty("id")
    private Long igdbId;

    /**
     * Nombre alternativo de la plataforma, si existe.
     */
    @Schema(description = "Nombre alternativo de la plataforma.", example = "PC", nullable = true)
    @JsonProperty("alternative_name")
    private String alternativeName;

    /**
     * Nombre principal de la plataforma.
     */
    @Schema(description = "Nombre principal de la plataforma.", example = "PC (Microsoft Windows)")
    private String name;

    /**
     * Logo de la plataforma.
     * Contiene el ID y la URL del logo.
     */
    @Schema(description = "Logo de la plataforma.")
    @JsonProperty("platform_logo")
    private PlatformLogoDto platformLogo;
}
