package mp.tfg.mycheckpoint.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO que representa un comentario público sobre un juego.
 * Incluye información del autor del comentario y el contenido del mismo.
 */
@Schema(description = "DTO que representa un comentario público sobre un juego.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicGameCommentDTO {

    /**
     * Nombre de usuario del autor del comentario.
     */
    @Schema(description = "Nombre de usuario del autor del comentario.", example = "criticoDeJuegos123")
    @JsonProperty("username")
    private String username;

    /**
     * ID público del usuario autor del comentario, para enlazar a su perfil público.
     */
    @Schema(description = "ID público del usuario autor del comentario.", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("user_public_id")
    private UUID userPublicId;

    /**
     * Texto del comentario realizado por el usuario.
     */
    @Schema(description = "Texto del comentario.", example = "¡Este juego es increíble! Lo recomiendo totalmente.")
    @JsonProperty("comment_text")
    private String commentText;

    /**
     * Fecha y hora en que se realizó o actualizó el comentario.
     * Se toma del campo `updatedAt` de la entidad UserGame.
     */
    @Schema(description = "Fecha y hora de la última actualización del comentario (proveniente de UserGame.updatedAt).", example = "2024-05-28T10:30:00.123Z")
    @JsonProperty("comment_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime commentDate;
}