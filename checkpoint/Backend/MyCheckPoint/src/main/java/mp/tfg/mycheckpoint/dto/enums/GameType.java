package mp.tfg.mycheckpoint.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Representa los diferentes tipos de productos de juego según la clasificación de IGDB.
 * Cada tipo tiene un valor numérico asociado utilizado por IGDB y un nombre descriptivo.
 */
public enum GameType {
    /**
     * Juego principal o base.
     */
    GAME(0, "Juego Principal"),
    /**
     * Contenido descargable adicional para un juego.
     */
    DLC(1, "Contenido Descargable"),
    /**
     * Expansión que añade contenido significativo a un juego base.
     */
    EXPANSION(2, "Expansión"),
    /**
     * Paquete que agrupa varios juegos o contenidos.
     */
    BUNDLE(3, "Paquete"),
    /**
     * Expansión que puede jugarse de forma independiente sin el juego base.
     */
    STANDALONE_EXPANSION(4, "Expansión Independiente"),
    /**
     * Modificación creada por la comunidad o terceros para un juego.
     */
    MOD(5, "Modificación"),
    /**
     * Un episodio dentro de un juego serializado o episódico.
     */
    EPISODE(6, "Episodio"),
    /**
     * Una temporada de contenido para un juego, a menudo en juegos como servicio.
     */
    SEASON(7, "Temporada"),
    /**
     * Recreación completa de un juego anterior con tecnología y/o mecánicas modernas.
     */
    REMAKE(8, "Remake"),
    /**
     * Versión actualizada de un juego anterior, generalmente con mejoras gráficas o de rendimiento.
     */
    REMASTER(9, "Remasterización"),
    /**
     * Juego que incluye contenido adicional sobre la versión original.
     */
    EXPANDED_GAME(10, "Juego Expandido"),
    /**
     * Adaptación de un juego a una plataforma diferente.
     */
    PORT(11, "Adaptación"),
    /**
     * Derivación o bifurcación de un juego existente.
     */
    FORK(12, "Derivación"),
    /**
     * Colección de juegos o contenidos, similar a Bundle.
     */
    PACK(13, "Colección"),
    /**
     * Actualización o parche para un juego.
     */
    UPDATE(14, "Actualización"),
    /**
     * Tipo de juego desconocido o no especificado.
     */
    UNKNOWN(15, "Desconocido");

    private final int value;
    private final String displayName;

    /**
     * Constructor para GameType.
     * @param value El valor numérico asociado con el tipo de juego (según IGDB).
     * @param displayName El nombre legible para mostrar del tipo de juego.
     */
    GameType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    /**
     * Obtiene el valor numérico del tipo de juego.
     * Usado por Jackson para la serialización a JSON.
     * @return El valor numérico.
     */
    @JsonValue
    public int getValue() {
        return value;
    }

    /**
     * Obtiene el nombre legible del tipo de juego.
     * @return El nombre para mostrar.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Crea una instancia de GameType a partir de su valor numérico.
     * Usado por Jackson para la deserialización desde JSON.
     * @param value El valor numérico a convertir.
     * @return La instancia de GameType correspondiente.
     * @throws IllegalArgumentException si el valor no corresponde a ningún GameType conocido.
     */
    @JsonCreator
    public static GameType fromValue(int value) {
        for (GameType type : GameType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Valor desconocido para GameType: " + value);
    }
}