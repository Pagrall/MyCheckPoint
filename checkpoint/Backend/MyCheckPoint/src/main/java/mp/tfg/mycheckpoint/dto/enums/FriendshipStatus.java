package mp.tfg.mycheckpoint.dto.enums; // O el paquete que elijas para enums de modelo

/**
 * Define los posibles estados de una relación de amistad entre usuarios.
 */
public enum FriendshipStatus {
    /**
     * La solicitud de amistad ha sido enviada y está esperando una respuesta.
     */
    PENDING,
    /**
     * La solicitud de amistad ha sido aceptada y los usuarios son amigos.
     */
    ACCEPTED,
    /**
     * La solicitud de amistad ha sido rechazada por el receptor.
     */
    DECLINED,
    /**
     * Un usuario ha bloqueado al otro. (Consideración para futuras mejoras)
     */
    BLOCKED
}