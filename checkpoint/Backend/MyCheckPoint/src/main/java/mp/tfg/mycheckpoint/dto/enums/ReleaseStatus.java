package mp.tfg.mycheckpoint.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enumera los posibles estados de lanzamiento de un juego, mapeados desde los valores de IGDB.
 */
public enum ReleaseStatus {
    /**
     * El juego ha sido lanzado oficialmente.
     */
    RELEASED(0, "Released"),
    /**
     * El juego está en fase Alfa de desarrollo.
     */
    ALPHA(2, "Alpha"),
    /**
     * El juego está en fase Beta de desarrollo.
     */
    BETA(3, "Beta"),
    /**
     * El juego está disponible como Acceso Anticipado.
     */
    EARLY_ACCESS(4, "Early Access"),
    /**
     * El juego está actualmente offline o no disponible temporalmente.
     */
    OFFLINE(5, "Offline"),
    /**
     * El lanzamiento del juego ha sido cancelado.
     */
    CANCELLED(6, "Cancelled"),
    /**
     * Se rumorea el lanzamiento del juego, pero no está confirmado.
     */
    RUMORED(7, "Rumored"),
    /**
     * El juego ha sido retirado de la venta o ya no está disponible.
     */
    DELISTED(8, "Delisted"),
    /**
     * Estado de lanzamiento desconocido o no especificado.
     */
    UNKNOWN(-1, "Unknown");

    private final int value;
    private final String displayName;

    private static final Logger logger = LoggerFactory.getLogger(ReleaseStatus.class);

    /**
     * Constructor para ReleaseStatus.
     * @param value El valor numérico asociado con el estado de lanzamiento.
     * @param displayName El nombre legible para mostrar del estado.
     */
    ReleaseStatus(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    /**
     * Obtiene el valor numérico del estado de lanzamiento.
     * Usado por Jackson para la serialización.
     * @return El valor numérico.
     */
    @JsonValue
    public int getValue() {
        return value;
    }

    /**
     * Obtiene el nombre legible del estado de lanzamiento.
     * @return El nombre para mostrar.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Mapea un valor entero de estado de IGDB al enum ReleaseStatus correspondiente.
     * IGDB usa: 0=NULL (mapeado a UNKNOWN), 1=RELEASED (mapeado a RELEASED(0)), 2=ALPHA, ..., 8=DELISTED.
     * Nótese que hay un desajuste en el valor para 'RELEASED' entre IGDB y este enum.
     * @param igdbStatusValue El valor de estado de IGDB.
     * @return El ReleaseStatus correspondiente, o UNKNOWN si no se puede mapear.
     */
    public static ReleaseStatus mapFromIgdbValue(Integer igdbStatusValue) {
        if (igdbStatusValue == null) {
            return UNKNOWN;
        }
        switch (igdbStatusValue) {
            case 0: return RELEASED;
            case 2: return ALPHA;
            case 3: return BETA;
            case 4: return EARLY_ACCESS;
            case 5: return OFFLINE;
            case 6: return CANCELLED;
            case 7: return RUMORED;
            case 8: return DELISTED;
            case -1: return UNKNOWN; // Caso explícito para el UNKNOWN de IGDB si es -1.
            default:
                logger.warn("Valor de status de IGDB desconocido recibido: {}", igdbStatusValue);
                return UNKNOWN;
        }
    }

    /**
     * Crea una instancia de ReleaseStatus a partir de su valor numérico interno.
     * Usado por Jackson para la deserialización si el JSON contiene los valores de este enum (0-8, -1).
     * @param value El valor numérico a convertir.
     * @return El ReleaseStatus correspondiente.
     */
    @JsonCreator
    public static ReleaseStatus fromValue(Integer value) {
        if (value == null) {
            return UNKNOWN;
        }
        for (ReleaseStatus status : ReleaseStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        logger.warn("Valor entero desconocido para crear ReleaseStatus directamente: {}", value);
        return UNKNOWN;
    }
}