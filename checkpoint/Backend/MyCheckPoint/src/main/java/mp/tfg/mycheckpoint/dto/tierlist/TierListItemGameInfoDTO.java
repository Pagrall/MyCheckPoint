package mp.tfg.mycheckpoint.dto.tierlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa la información de un juego dentro de un ítem de una Tier List.
 * Contiene detalles básicos del juego como ID, nombre y carátula, así como
 * el ID del ítem de la Tier List y el ID de la entrada UserGame asociada.
 */
@Schema(description = "DTO con información de un juego para un ítem de Tier List.") // Schema a nivel de clase
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierListItemGameInfoDTO {

    /**
     * ID interno del ítem en la Tier List (TierListItem).
     */
    @Schema(description = "ID interno del ítem en la Tier List.", example = "201") // Schema para el campo
    @JsonProperty("tier_list_item_id")
    private Long tierListItemId;

    /**
     * ID interno de la entrada del juego en la biblioteca del usuario (UserGame).
     */
    @Schema(description = "ID interno de la entrada UserGame asociada.", example = "101") // Schema para el campo
    @JsonProperty("user_game_id")
    private Long userGameId;

    /**
     * ID de IGDB del juego.
     */
    @Schema(description = "ID de IGDB del juego.", example = "1020") // Schema para el campo
    @JsonProperty("game_igdb_id")
    private Long gameIgdbId;

    /**
     * Nombre del juego.
     */
    @Schema(description = "Nombre del juego.", example = "The Legend of Zelda: Breath of the Wild") // Schema para el campo
    @JsonProperty("game_name")
    private String gameName;

    /**
     * URL de la carátula del juego. Puede ser nula.
     */
    @Schema(description = "URL de la carátula del juego.", example = "//images.igdb.com/igdb/image/upload/t_cover_big/co1vja.jpg", nullable = true) // Schema para el campo
    @JsonProperty("game_cover_url")
    private String gameCoverUrl;

    /**
     * Orden del ítem dentro de su sección en la Tier List.
     */
    @Schema(description = "Orden del ítem dentro de su sección.", example = "0") // Schema para el campo
    @JsonProperty("item_order")
    private int itemOrder;
}