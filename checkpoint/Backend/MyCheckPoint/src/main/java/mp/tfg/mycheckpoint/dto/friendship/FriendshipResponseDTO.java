package mp.tfg.mycheckpoint.dto.friendship;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mp.tfg.mycheckpoint.dto.enums.FriendshipStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO que representa el estado de una amistad o una solicitud de amistad en una respuesta.
 * Contiene información sobre el otro usuario implicado en la relación, el estado
 * de la amistad y metadatos relevantes como fechas de creación y actualización.
 */
@Schema(description = "DTO que representa el estado de una amistad o solicitud de amistad.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipResponseDTO {

    /**
     * ID interno único de la entidad Friendship que representa esta relación o solicitud.
     */
    @Schema(description = "ID interno de la relación de amistad/solicitud.", example = "101")
    @JsonProperty("friendship_id")
    private Long friendshipId;

    /**
     * ID público del otro usuario implicado en esta relación de amistad.
     * Puede ser el amigo, el solicitante o el receptor de una solicitud.
     */
    @Schema(description = "ID público del otro usuario en la relación (amigo o solicitante/receptor).", example = "b2c3d4e5-f6a7-8901-2345-67890abcdef1")
    @JsonProperty("user_public_id")
    private UUID userPublicId;

    /**
     * Nombre de usuario del otro usuario implicado en la relación.
     */
    @Schema(description = "Nombre de usuario del otro usuario en la relación.", example = "amigoUsuario")
    @JsonProperty("username")
    private String username;

    /**
     * URL de la foto de perfil del otro usuario.
     * Puede ser nulo si el usuario no tiene una foto de perfil configurada.
     */
    @Schema(description = "URL de la foto de perfil del otro usuario. Puede ser nulo.", example = "/profile-pictures/b2c3d4e5-f6a7-8901-2345-67890abcdef1.jpg", nullable = true)
    @JsonProperty("profile_picture_url")
    private String profilePictureUrl;

    /**
     * Estado actual de la relación de amistad (ej. PENDIENTE, ACEPTADA).
     */
    @Schema(description = "Estado actual de la amistad o solicitud.", example = "PENDING", allowableValues = {"PENDING", "ACCEPTED", "DECLINED", "BLOCKED"})
    @JsonProperty("status")
    private FriendshipStatus status;

    /**
     * Indica si el usuario autenticado (el que recibe esta respuesta) fue quien
     * inició originalmente la solicitud de amistad.
     * Es {@code true} si el usuario actual es el {@code requester} en la entidad Friendship,
     * y {@code false} si es el {@code receiver}. Esto ayuda al frontend a contextualizar la solicitud.
     */
    @Schema(description = "Indica si el usuario autenticado fue quien inició originalmente la solicitud de amistad. " +
            "True si el usuario actual es el 'requester', False si es el 'receiver'.", example = "true")
    @JsonProperty("is_initiated_by_current_user")
    private Boolean isInitiatedByCurrentUser;

    /**
     * Fecha y hora en que se creó la solicitud de amistad o se estableció la amistad.
     * Formato ISO 8601.
     */
    @Schema(description = "Fecha y hora de creación de la solicitud o de cuando se estableció la amistad.", format = "date-time", example = "2024-05-27T10:00:00.000Z")
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del estado de la amistad/solicitud
     * (ej. cuándo fue aceptada o modificada por última vez).
     * Formato ISO 8601.
     */
    @Schema(description = "Fecha y hora de la última actualización del estado (ej. aceptación).", format = "date-time", example = "2024-05-27T10:05:00.000Z")
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime updatedAt;
}