package mp.tfg.mycheckpoint.dto.enums;

/**
 * Define los diferentes estados en los que un usuario puede tener un juego en su biblioteca personal.
 */
public enum UserGameStatus {
    /**
     * El usuario ha completado el juego en alguna medida general.
     */
    COMPLETED,
    /**
     * El usuario ha completado la historia principal del juego.
     */
    COMPLETED_MAIN_STORY,
    /**
     * El usuario ha completado la historia principal y el contenido secundario principal.
     */
    COMPLETED_MAIN_AND_SIDES,
    /**
     * El usuario ha completado el juego al 100%.
     */
    COMPLETED_100_PERCENT,
    /**
     * El juego está archivado, sin especificar la razón.
     */
    ARCHIVED,
    /**
     * El juego ha sido archivado porque el usuario lo abandonó.
     */
    ARCHIVED_ABANDONED,
    /**
     * El juego ha sido archivado porque el usuario no lo está jugando actualmente (ej. ya no tiene acceso).
     */
    ARCHIVED_NOT_PLAYING,
    /**
     * El juego está en la lista de deseados del usuario.
     */
    WISHLIST,
    /**
     * El usuario está jugando activamente el juego.
     */
    PLAYING,
    /**
     * El usuario estaba jugando el juego, pero está actualmente en pausa.
     */
    PLAYING_PAUSED,
    /**
     * El usuario está jugando un juego que no tiene un final definido (ej. MMOs, multijugador continuo).
     */
    PLAYING_ENDLESS
}