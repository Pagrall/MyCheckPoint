package mp.tfg.mycheckpoint.dto.usergame;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mp.tfg.mycheckpoint.dto.enums.UserGamePersonalPlatform;
import mp.tfg.mycheckpoint.dto.enums.UserGameStatus;

import java.time.LocalDate;

/**
 * DTO para proporcionar o actualizar los datos específicos de un usuario para un juego en su biblioteca.
 * Contiene información como el estado del juego, plataforma personal, puntuación, comentarios y fechas.
 * El ID del juego (igdbId) se maneja generalmente como un parámetro de ruta en el controlador, no en este DTO.
 */
@Schema(description = "DTO para proporcionar o actualizar los datos específicos de un usuario para un juego en su biblioteca.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGameDataDTO {

    /**
     * Estado actual del juego en la biblioteca del usuario (ej. JUGANDO, COMPLETADO).
     * Opcional en la actualización.
     */
    @Schema(description = "Estado del juego en la biblioteca del usuario (ej. JUGANDO, COMPLETADO).", example = "PLAYING", nullable = true)
    @JsonProperty("status")
    private UserGameStatus status;

    /**
     * Plataforma personal en la que el usuario juega o posee el juego.
     * Opcional en la actualización.
     */
    @Schema(description = "Plataforma personal en la que el usuario juega o posee el juego.", example = "STEAM", nullable = true)
    @JsonProperty("personal_platform")
    private UserGamePersonalPlatform personalPlatform;

    /**
     * Indica si el usuario posee físicamente o digitalmente el juego.
     * Opcional en la actualización.
     */
    @Schema(description = "Indica si el usuario posee físicamente o digitalmente el juego.", example = "true", nullable = true)
    @JsonProperty("has_possession")
    private Boolean hasPossession;

    /**
     * Puntuación personal otorgada por el usuario al juego (ej. de 0.0 a 10.0).
     * Debe estar dentro del rango definido si se proporciona. Opcional en la actualización.
     */
    @Schema(description = "Puntuación personal otorgada por el usuario al juego (ej. de 0.0 a 10.0).", example = "8.5", minimum = "0", maximum = "10", nullable = true)
    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 10, message = "Score must be at most 10")
    @JsonProperty("score")
    private Float score;

    /**
     * Comentario público del usuario sobre el juego.
     * Longitud máxima de 2000 caracteres. Opcional en la actualización.
     */
    @Schema(description = "Comentario público del usuario sobre el juego. Máximo 2000 caracteres.", example = "¡Un gran juego!", maxLength = 2000, nullable = true)
    @Size(max = 2000, message = "Comment cannot exceed 2000 characters")
    @JsonProperty("comment")
    private String comment;

    /**
     * Comentario privado del usuario sobre el juego (solo visible para él).
     * Longitud máxima de 2000 caracteres. Opcional en la actualización.
     */
    @Schema(description = "Comentario privado del usuario sobre el juego (solo visible para él). Máximo 2000 caracteres.", example = "Recordar farmear X item.", maxLength = 2000, nullable = true)
    @Size(max = 2000, message = "Private comment cannot exceed 2000 characters")
    @JsonProperty("private_comment")
    private String privateComment;

    /**
     * Fecha en la que el usuario comenzó a jugar el juego.
     * Formato: YYYY-MM-DD. Opcional en la actualización.
     */
    @Schema(description = "Fecha en la que el usuario comenzó a jugar el juego (formato YYYY-MM-DD).", example = "2024-01-15", format = "date", nullable = true)
    @JsonProperty("start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * Fecha en la que el usuario terminó de jugar el juego.
     * Formato: YYYY-MM-DD. Opcional en la actualización.
     */
    @Schema(description = "Fecha en la que el usuario terminó de jugar el juego (formato YYYY-MM-DD).", example = "2024-03-20", format = "date", nullable = true)
    @JsonProperty("end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * Duración estimada en horas para completar la historia principal.
     * Debe ser un valor no negativo si se proporciona. Opcional en la actualización.
     */
    @Schema(description = "Duración estimada en horas para completar la historia principal.", example = "40.5", minimum = "0", nullable = true)
    @Min(value = 0, message = "Duration must be non-negative")
    @JsonProperty("story_duration_hours")
    private Float storyDurationHours;

    /**
     * Duración estimada en horas para completar la historia principal y misiones secundarias importantes.
     * Debe ser un valor no negativo si se proporciona. Opcional en la actualización.
     */
    @Schema(description = "Duración estimada en horas para completar la historia principal y misiones secundarias importantes.", example = "65.0", minimum = "0", nullable = true)
    @Min(value = 0, message = "Duration must be non-negative")
    @JsonProperty("story_secondary_duration_hours")
    private Float storySecondaryDurationHours;

    /**
     * Duración estimada en horas para completar el juego al 100%.
     * Debe ser un valor no negativo si se proporciona. Opcional en la actualización.
     */
    @Schema(description = "Duración estimada en horas para completar el juego al 100%.", example = "120.0", minimum = "0", nullable = true)
    @Min(value = 0, message = "Duration must be non-negative")
    @JsonProperty("completionist_duration_hours")
    private Float completionistDurationHours;
}
