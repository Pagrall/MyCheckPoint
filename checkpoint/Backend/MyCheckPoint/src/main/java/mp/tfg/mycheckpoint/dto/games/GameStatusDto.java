package mp.tfg.mycheckpoint.dto.games;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa el estado de lanzamiento de un juego,
 * utilizando el ID numérico proporcionado por IGDB.
 * Este ID se mapea internamente al enum {@link mp.tfg.mycheckpoint.dto.enums.ReleaseStatus}.
 */
@Schema(description = "DTO para el estado de lanzamiento de un juego (ID numérico de IGDB).")
@Data
@NoArgsConstructor
public class GameStatusDto {

    /**
     * El ID numérico del estado del juego según IGDB.
     * Por ejemplo: 0 (RELEASED), 2 (ALPHA), 3 (BETA), etc.
     * Ver la documentación de IGDB o el enum {@link mp.tfg.mycheckpoint.dto.enums.ReleaseStatus#mapFromIgdbValue(Integer)}
     * para el mapeo completo.
     */
    @Schema(description = "ID numérico del estado del juego según IGDB.", example = "0")
    @JsonProperty("id")
    private Integer id;
}
