package mp.tfg.mycheckpoint.dto.enums;

/**
 * Define los diferentes tipos de Tier Lists que pueden existir en la aplicación.
 */
public enum TierListType {
    /**
     * Tier List general asociada al perfil del usuario, no vinculada a una GameList específica.
     * Los ítems se añaden manualmente desde la biblioteca del usuario.
     */
    PROFILE_GLOBAL,
    /**
     * Tier List generada automáticamente a partir de una GameList específica.
     * Los ítems se sincronizan con los juegos de la GameList asociada.
     */
    FROM_GAMELIST
}