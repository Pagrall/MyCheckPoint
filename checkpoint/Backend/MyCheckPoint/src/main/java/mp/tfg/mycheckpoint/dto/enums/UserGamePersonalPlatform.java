package mp.tfg.mycheckpoint.dto.enums;


/**
 * Enumera las plataformas personales en las que un usuario puede poseer o jugar un juego.
 * Esto permite al usuario especificar dónde tiene un juego en particular.
 */
public enum UserGamePersonalPlatform {
    /**
     * Plataforma Steam de Valve.
     */
    STEAM,
    /**
     * Plataforma Epic Games Store.
     */
    EPIC_GAMES,
    /**
     * Plataforma GOG Galaxy de CD Projekt.
     */
    GOG_GALAXY,
    /**
     * Consolas y servicios Xbox de Microsoft.
     */
    XBOX,
    /**
     * Consolas y servicios PlayStation de Sony.
     */
    PLAYSTATION,
    /**
     * Consolas y servicios Nintendo.
     */
    NINTENDO,
    /**
     * Plataforma Battle.net de Blizzard Entertainment.
     */
    BATTLE_NET,
    /**
     * Plataforma EA App (anteriormente Origin) de Electronic Arts.
     */
    EA_APP,
    /**
     * Plataforma Ubisoft Connect (anteriormente Uplay) de Ubisoft.
     */
    UBISOFT_CONNECT,
    /**
     * Otra plataforma no listada específicamente.
     */
    OTHER
}
