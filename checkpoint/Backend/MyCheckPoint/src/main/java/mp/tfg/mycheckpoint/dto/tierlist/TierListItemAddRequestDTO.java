package mp.tfg.mycheckpoint.dto.tierlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de añadir un ítem (un juego de la biblioteca del usuario)
 * a una sección específica de una Tier List.
 */
@Schema(description = "DTO para añadir un ítem (juego de la biblioteca del usuario) a una sección de una Tier List.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TierListItemAddRequestDTO {

    /**
     * ID interno de la entrada 'UserGame' (representa un juego específico
     * en la biblioteca del usuario) que se desea añadir o mover a la Tier List.
     * Este campo es obligatorio.
     */
    @Schema(description = "ID interno de la entrada 'UserGame' (juego en la biblioteca del usuario) que se desea añadir o mover. Es obligatorio.",
            example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID de UserGame no puede ser nulo.")
    @JsonProperty("user_game_id")
    private Long userGameId;

    /**
     * Posición (orden basado en cero) deseada para el ítem dentro de la sección destino.
     * Si es nulo, está fuera de rango o no se proporciona, el ítem se añadirá al final de la sección.
     * Este campo es opcional.
     */
    @Schema(description = "Posición (orden basado en cero) deseada para el ítem dentro de la sección destino. " +
            "Si es nulo o está fuera de rango, el ítem se añadirá al final de la sección. Opcional.",
            example = "0", nullable = true)
    @JsonProperty("order")
    private Integer order;
}