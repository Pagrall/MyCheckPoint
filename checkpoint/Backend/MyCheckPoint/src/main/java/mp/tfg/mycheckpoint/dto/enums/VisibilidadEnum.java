package mp.tfg.mycheckpoint.dto.enums;

/**
 * Enumera los niveles de visibilidad que pueden aplicarse a diferentes entidades
 * dentro de la aplicación, como perfiles de usuario o listas de juegos.
 */
public enum VisibilidadEnum {
    /**
     * El contenido es visible para cualquier usuario, autenticado o no.
     */
    PUBLICO,
    /**
     * El contenido es visible solo para el propietario.
     */
    PRIVADO,
    /**
     * El contenido es visible solo para el propietario y sus amigos.
     */
    SOLO_AMIGOS
}