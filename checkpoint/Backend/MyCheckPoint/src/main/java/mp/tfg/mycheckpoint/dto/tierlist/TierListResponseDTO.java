package mp.tfg.mycheckpoint.dto.tierlist;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mp.tfg.mycheckpoint.dto.enums.TierListType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO que representa una Tier List completa en una respuesta.
 * Incluye metadatos de la lista, el propietario, el tipo, su visibilidad,
 * las secciones definidas por el usuario y una sección especial para ítems sin clasificar.
 */
@Schema(description = "DTO que representa una Tier List, incluyendo sus secciones y los ítems (juegos) clasificados en ellas.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierListResponseDTO {

    /**
     * ID público único de la Tier List.
     * Este campo es de solo lectura.
     */
    @Schema(description = "ID público único de la Tier List.", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef", format = "uuid", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("public_id")
    private UUID publicId;

    /**
     * Nombre de la Tier List.
     */
    @Schema(description = "Nombre de la Tier List.", example = "Ranking de Juegos de Pelea")
    @JsonProperty("name")
    private String name;

    /**
     * Descripción detallada de la Tier List. Puede ser nula.
     */
    @Schema(description = "Descripción detallada de la Tier List.", example = "Mi ranking personal de juegos de pelea basado en su impacto y jugabilidad.", nullable = true)
    @JsonProperty("description")
    private String description;

    /**
     * Tipo de Tier List, indicando si es general del perfil o generada a partir de una GameList.
     * Este campo es de solo lectura.
     */
    @Schema(description = "Tipo de Tier List (ej. general de perfil o basada en una GameList).", example = "PROFILE_GLOBAL", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("type")
    private TierListType type;

    /**
     * ID público de la GameList origen, si esta Tier List se generó a partir de una.
     * Será nulo para Tier Lists de perfil global. Este campo es de solo lectura.
     */
    @Schema(description = "ID público de la GameList origen, si esta Tier List se generó a partir de una. Nulo para Tier Lists de perfil global.",
            example = "b2c3d4e5-f6a7-8901-2345-67890abcdef1", format = "uuid", nullable = true, accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("source_game_list_public_id")
    private UUID sourceGameListPublicId;

    /**
     * Nombre de usuario del propietario de la Tier List.
     * Este campo es de solo lectura.
     */
    @Schema(description = "Nombre de usuario del propietario de la Tier List.", example = "jugadorExperto", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("owner_username")
    private String ownerUsername;

    /**
     * Indica si la Tier List es pública (true) o privada (false).
     */
    @Schema(description = "Indica si la Tier List es pública (true) o privada (false).", example = "true")
    @JsonProperty("is_public")
    private boolean isPublic;

    /**
     * Lista de secciones (tiers) definidas por el usuario, ordenadas según su {@code sectionOrder}.
     * No incluye la sección especial "Juegos por Clasificar". Puede estar vacía.
     */
    @Schema(description = "Lista de secciones (tiers) definidas por el usuario, ordenadas. No incluye la sección 'Sin Clasificar'.", nullable = true)
    @JsonProperty("sections")
    private List<TierSectionResponseDTO> sections;

    /**
     * Sección especial que contiene los ítems (juegos) que aún no han sido clasificados
     * en ninguna de las tiers definidas por el usuario.
     * Usualmente se llama "Juegos por Clasificar" y tiene un orden predefinido (ej. 0).
     * Puede ser nula si no hay ítems sin clasificar o si la estructura de la Tier List no la requiere.
     */
    @Schema(description = "Sección especial para ítems (juegos) que aún no han sido clasificados en ninguna tier. " +
            "Contiene el nombre 'Juegos por Clasificar' y orden 0.", nullable = true)
    @JsonProperty("unclassified_section")
    private TierSectionResponseDTO unclassifiedSection;

    /**
     * Fecha y hora de creación de la Tier List.
     * Formato ISO 8601. Este campo es de solo lectura.
     */
    @Schema(description = "Fecha y hora de creación de la Tier List (formato ISO 8601).", example = "2024-05-20T10:00:00.000Z", format = "date-time", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de la Tier List.
     * Formato ISO 8601. Este campo es de solo lectura.
     */
    @Schema(description = "Fecha y hora de la última actualización de la Tier List (formato ISO 8601).", example = "2024-05-21T15:30:00.000Z", format = "date-time", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime updatedAt;
}