package mp.tfg.mycheckpoint.dto.usergame;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mp.tfg.mycheckpoint.dto.enums.UserGamePersonalPlatform;
import mp.tfg.mycheckpoint.dto.enums.UserGameStatus;
import mp.tfg.mycheckpoint.dto.games.CoverDto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * DTO que representa una entrada en la biblioteca de juegos de un usuario.
 * Incluye los datos personales que el usuario ha registrado para un juego específico,
 * como estado, puntuación, comentarios, plataforma y fechas.
 */
@Schema(description = "DTO que representa una entrada en la biblioteca de juegos de un usuario, incluyendo sus datos personales sobre un juego específico.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGameResponseDTO {

    @JsonProperty("internal_id")
    private Long internalId;
    /**
     * ID de IGDB del juego al que se refiere esta entrada de la biblioteca.
     * Utilizado para identificar el juego globalmente.
     */
    @Schema(description = "ID de IGDB del juego al que se refiere esta entrada de la biblioteca.", example = "1020")
    @JsonProperty("game_igdb_id")
    private Long gameIgdbId;


    @Schema(description = "Nombre del juego.")
    @JsonProperty("game_name")
    private String gameName;

    @Schema(description = "Información de la carátula del juego.")
    @JsonProperty("game_cover")
    private CoverDto gameCover;

    /**
     * Estado actual del juego según el usuario (ej. COMPLETADO_HISTORIA, JUGANDO).
     */
    @Schema(description = "Estado actual del juego en la biblioteca del usuario.", example = "COMPLETED_MAIN_STORY")
    @JsonProperty("status")
    private UserGameStatus status;

    /**
     * Plataforma personal en la que el usuario juega o posee el juego.
     */
    @Schema(description = "Plataforma personal en la que el usuario juega o posee el juego.", example = "PC_STEAM")
    @JsonProperty("personal_platform")
    private UserGamePersonalPlatform personalPlatform;

    /**
     * Indica si el usuario posee el juego (física o digitalmente).
     */
    @Schema(description = "Indica si el usuario posee el juego.", example = "true")
    @JsonProperty("has_possession")
    private Boolean hasPossession;

    /**
     * Puntuación personal otorgada por el usuario al juego (ej. de 0.0 a 10.0).
     * Puede ser nulo si no se ha asignado puntuación.
     */
    @Schema(description = "Puntuación personal otorgada por el usuario al juego.", example = "9.0", minimum = "0", maximum = "10")
    @JsonProperty("score")
    private Float score;

    /**
     * Comentario público del usuario sobre el juego.
     * Puede ser nulo si no hay comentario.
     */
    @Schema(description = "Comentario público del usuario sobre el juego.", example = "Un clásico indispensable.", nullable = true)
    @JsonProperty("comment")
    private String comment;

    /**
     * Comentario privado del usuario sobre el juego, visible solo para él.
     * Puede ser nulo si no hay comentario privado.
     */
    @Schema(description = "Comentario privado del usuario sobre el juego (no visible para otros).", example = "Intentar el final alternativo en la próxima partida.", nullable = true)
    @JsonProperty("private_comment")
    private String privateComment;

    /**
     * Fecha en que el usuario comenzó a jugar el juego (formato YYYY-MM-DD).
     * Puede ser nula.
     */
    @Schema(description = "Fecha en que el usuario comenzó a jugar (YYYY-MM-DD).", example = "2023-05-10", format = "date", nullable = true)
    @JsonProperty("start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * Fecha en que el usuario terminó de jugar el juego (formato YYYY-MM-DD).
     * Puede ser nula.
     */
    @Schema(description = "Fecha en que el usuario terminó de jugar (YYYY-MM-DD).", example = "2023-07-15", format = "date", nullable = true)
    @JsonProperty("end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * Horas estimadas dedicadas a la historia principal del juego.
     * Puede ser nulo.
     */
    @Schema(description = "Horas dedicadas a la historia principal.", example = "55.5", nullable = true)
    @JsonProperty("story_duration_hours")
    private Float storyDurationHours;

    /**
     * Horas estimadas dedicadas a la historia principal y contenido secundario.
     * Puede ser nulo.
     */
    @Schema(description = "Horas dedicadas a la historia principal y secundarias.", example = "80.0", nullable = true)
    @JsonProperty("story_secondary_duration_hours")
    private Float storySecondaryDurationHours;

    /**
     * Horas estimadas dedicadas para completar el juego al 100%.
     * Puede ser nulo.
     */
    @Schema(description = "Horas dedicadas para completar el juego al 100%.", example = "150.0", nullable = true)
    @JsonProperty("completionist_duration_hours")
    private Float completionistDurationHours;

    /**
     * Fecha y hora de creación de esta entrada en la biblioteca del usuario.
     * Formato ISO 8601. Este campo es de solo lectura.
     */
    @Schema(description = "Fecha y hora de creación de esta entrada en la biblioteca.", format = "date-time", example = "2023-05-10T12:00:00.000Z", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de esta entrada en la biblioteca.
     * Formato ISO 8601. Este campo es de solo lectura.
     */
    @Schema(description = "Fecha y hora de la última actualización de esta entrada.", format = "date-time", example = "2023-07-15T18:30:00.000Z", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime updatedAt;
}
