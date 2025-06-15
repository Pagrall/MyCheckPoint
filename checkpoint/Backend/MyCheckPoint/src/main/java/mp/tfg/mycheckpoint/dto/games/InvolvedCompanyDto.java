package mp.tfg.mycheckpoint.dto.games;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO que representa la relación de una compañía con un juego,
 * indicando su rol (desarrolladora, editora, etc.).
 */
@Schema(description = "DTO para la relación de una compañía con un juego y su rol.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvolvedCompanyDto {
    /**
     * ID único de esta relación de involucramiento, generalmente proveniente de IGDB.
     */
    @Schema(description = "ID de la relación de involucramiento desde IGDB.", example = "130475")
    @JsonProperty("id")
    private Long involvementIgdbId;

    /**
     * Información básica de la compañía involucrada.
     */
    @Schema(description = "Información de la compañía involucrada.")
    private CompanyInfoDto company;

    /**
     * Indica si la compañía actuó como desarrolladora del juego.
     */
    @Schema(description = "True si la compañía es desarrolladora.", example = "true")
    private boolean developer;

    /**
     * Indica si la compañía actuó en la portabilidad (porting) del juego.
     */
    @Schema(description = "True si la compañía realizó la portabilidad del juego.", example = "false")
    private boolean porting;

    /**
     * Indica si la compañía actuó como editora (publisher) del juego.
     */
    @Schema(description = "True si la compañía es editora.", example = "true")
    private boolean publisher;

    /**
     * Indica si la compañía dio soporte (supporting) al desarrollo del juego.
     */
    @Schema(description = "True si la compañía dio soporte al desarrollo.", example = "false")
    private boolean supporting;
}
