package mp.tfg.mycheckpoint.dto.usergame;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mp.tfg.mycheckpoint.dto.comment.PublicGameCommentDTO;
import mp.tfg.mycheckpoint.dto.games.GameDto;

import java.util.List;

/**
 * DTO que agrega información detallada sobre un juego.
 * Combina la información general del juego ({@link GameDto}),
 * los datos específicos del usuario para ese juego ({@link UserGameResponseDTO}) si el usuario está autenticado
 * y tiene el juego en su biblioteca, y una lista de comentarios públicos ({@link PublicGameCommentDTO}).
 */
@Schema(description = "DTO que contiene los detalles completos de un juego, incluyendo información general, " +
        "datos específicos del usuario (si está autenticado y el juego está en su biblioteca), " +
        "y comentarios públicos.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDetailDTO {

    /**
     * Información general y detallada del juego.
     * Proviene de la base de datos local (potencialmente sincronizada con IGDB).
     */
    @Schema(description = "Información general y detallada del juego (obtenida de IGDB y/o base de datos local).")
    @JsonProperty("game_info")
    private GameDto gameInfo;

    /**
     * Datos específicos del usuario para este juego, como estado, puntuación, comentarios privados, etc.
     * Este campo será nulo si el usuario no está autenticado, si el juego no está en su biblioteca,
     * o si no hay datos específicos registrados por el usuario para este juego.
     * Solo se incluye en la respuesta JSON si no es nulo.
     */
    @Schema(description = "Datos específicos del usuario para este juego (estado, puntuación, comentarios privados, etc.). " +
            "Este campo será nulo si el usuario no está autenticado o si el juego no está en su biblioteca.",
            nullable = true)
    @JsonProperty("user_game_data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserGameResponseDTO userGameData;

    /**
     * Lista de comentarios públicos realizados por otros usuarios sobre este juego.
     * Puede estar vacía si no hay comentarios o si el juego no tiene una entrada local para asociar comentarios.
     * Solo se incluye en la respuesta JSON si la lista no está vacía.
     */
    @Schema(description = "Lista de comentarios públicos realizados por otros usuarios sobre este juego. " +
            "Puede estar vacía si no hay comentarios o si el juego no existe en la base de datos local para asociar comentarios.",
            nullable = true)
    @JsonProperty("public_comments")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<PublicGameCommentDTO> publicComments;

}
