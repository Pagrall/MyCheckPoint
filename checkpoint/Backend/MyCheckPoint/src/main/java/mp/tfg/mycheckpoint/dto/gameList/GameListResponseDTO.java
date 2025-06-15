package mp.tfg.mycheckpoint.dto.gameList;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mp.tfg.mycheckpoint.dto.usergame.UserGameResponseDTO;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO que representa una lista de juegos personalizada.
 * Incluye sus detalles, el propietario, los juegos que contiene y metadatos como fechas de creación/actualización.
 */
@Schema(description = "DTO que representa una lista de juegos personalizada, incluyendo sus detalles y los juegos que contiene.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameListResponseDTO {

    /**
     * ID público único de la lista de juegos.
     * Este campo es de solo lectura.
     */
    @Schema(description = "ID público único de la lista de juegos.", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479", format = "uuid", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("public_id")
    private UUID publicId;

    /**
     * Nombre de la lista de juegos.
     */
    @Schema(description = "Nombre de la lista de juegos.", example = "Mis Juegos Favoritos de RPG")
    @JsonProperty("name")
    private String name;

    /**
     * Descripción de la lista de juegos. Puede ser nula.
     */
    @Schema(description = "Descripción de la lista de juegos.", example = "Una colección de los RPGs que más he disfrutado.", nullable = true)
    @JsonProperty("description")
    private String description;

    /**
     * Indica si la lista de juegos es pública (true) o privada (false).
     */
    @Schema(description = "Indica si la lista de juegos es pública (true) o privada (false).", example = "false")
    @JsonProperty("is_public")
    private boolean isPublic;

    /**
     * Nombre de usuario del propietario de la lista.
     * Este campo es de solo lectura.
     */
    @Schema(description = "Nombre de usuario del propietario de la lista.", example = "usuarioEjemplo", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("owner_username")
    private String ownerUsername;

    /**
     * Lista de juegos (representados por {@link UserGameResponseDTO}) incluidos en esta lista.
     * Puede estar vacía si la lista no contiene juegos.
     */
    @Schema(description = "Lista de juegos (entradas de la biblioteca del usuario) incluidos en esta lista. Puede estar vacía.", nullable = true)
    @JsonProperty("games_in_list")
    private List<UserGameResponseDTO> gamesInList;

    /**
     * Número total de juegos en la lista.
     * Este campo es de solo lectura.
     */
    @Schema(description = "Número total de juegos en la lista.", example = "5", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("game_count")
    private int gameCount;

    /**
     * Fecha y hora de creación de la lista.
     * Formato ISO 8601. Este campo es de solo lectura.
     */
    @Schema(description = "Fecha y hora de creación de la lista (formato ISO 8601).", example = "2024-05-01T10:00:00.000Z", format = "date-time", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de la lista.
     * Formato ISO 8601. Este campo es de solo lectura.
     */
    @Schema(description = "Fecha y hora de la última actualización de la lista (formato ISO 8601).", example = "2024-05-15T14:30:00.000Z", format = "date-time", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime updatedAt;
}
