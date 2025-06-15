package mp.tfg.mycheckpoint.dto.tierlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para especificar el movimiento de un ítem (juego) dentro de una Tier List.
 * Permite mover un ítem a una nueva sección y/o a una nueva posición dentro de esa sección.
 */
@Schema(description = "DTO para especificar el movimiento de un ítem (juego) a una nueva sección y/o posición dentro de una Tier List.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TierListItemMoveRequestDTO {

    /**
     * ID interno de la sección (tier) destino a la que se moverá el ítem.
     * Este campo es obligatorio.
     */
    @Schema(description = "ID interno de la sección (tier) destino a la que se moverá el ítem. Es obligatorio.",
            example = "103", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID de la sección destino no puede ser nulo.")
    @JsonProperty("target_section_internal_id")
    private Long targetSectionInternalId;

    /**
     * Nueva posición (orden basado en cero) para el ítem dentro de la sección destino.
     * Este campo es obligatorio.
     */
    @Schema(description = "Nueva posición (orden basado en cero) para el ítem dentro de la sección destino. Es obligatorio.",
            example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El nuevo orden no puede ser nulo.")
    @JsonProperty("new_order")
    private Integer newOrder;
}