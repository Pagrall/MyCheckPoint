package mp.tfg.mycheckpoint.dto.gameList;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de añadir un juego existente de la biblioteca del usuario
 * a una de sus listas de juegos personalizadas.
 */
@Schema(description = "DTO para añadir un juego existente de la biblioteca del usuario a una lista de juegos personalizada.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddGameToCustomListRequestDTO {

    /**
     * ID interno de la entrada 'UserGame' (juego en la biblioteca del usuario) que se desea añadir a la lista.
     * Este campo es obligatorio.
     */
    @Schema(description = "ID interno de la entrada 'UserGame' (juego en la biblioteca del usuario) que se desea añadir a la lista. Es obligatorio.",
            example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "UserGame ID cannot be null")
    @JsonProperty("user_game_id")
    private Long userGameId;
}
