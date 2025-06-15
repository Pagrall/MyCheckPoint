package mp.tfg.mycheckpoint.service;

import mp.tfg.mycheckpoint.dto.friendship.FriendshipResponseDTO;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz para el servicio de gestión de amistades.
 * Define las operaciones para enviar, aceptar, rechazar solicitudes de amistad,
 * eliminar amigos y listar diferentes estados de relaciones de amistad.
 */
public interface FriendshipService {

    /**
     * Permite al usuario ({@code senderUserEmail}) enviar una solicitud de amistad
     * al usuario identificado por {@code receiverUserPublicId}.
     * Si ya existe una solicitud pendiente del receptor hacia el emisor, la amistad
     * se aceptará automáticamente.
     *
     * @param senderUserEmail Email del usuario que envía la solicitud.
     * @param receiverUserPublicId UUID público del usuario que recibe la solicitud.
     * @return {@link FriendshipResponseDTO} representando el estado de la nueva solicitud o amistad.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si alguno de los usuarios no se encuentra.
     * @throws mp.tfg.mycheckpoint.exception.DuplicateEntryException Si ya existe una amistad o una solicitud pendiente.
     * @throws IllegalArgumentException Si el emisor intenta enviarse una solicitud a sí mismo.
     */
    FriendshipResponseDTO sendFriendRequest(String senderUserEmail, UUID receiverUserPublicId);

    /**
     * Permite al usuario actual ({@code currentUserEmail}) aceptar una solicitud de amistad
     * pendiente enviada por el usuario identificado por {@code requesterUserPublicId}.
     *
     * @param currentUserEmail Email del usuario que acepta la solicitud (debe ser el receptor de la solicitud).
     * @param requesterUserPublicId UUID público del usuario que envió la solicitud de amistad.
     * @return {@link FriendshipResponseDTO} representando la amistad aceptada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si no se encuentra la solicitud pendiente o alguno de los usuarios.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si el usuario actual no es el receptor de la solicitud.
     */
    FriendshipResponseDTO acceptFriendRequest(String currentUserEmail, UUID requesterUserPublicId);

    /**
     * Permite al usuario actual ({@code currentUserEmail}) rechazar o cancelar una solicitud de amistad pendiente.
     * Si el usuario actual es el receptor, la rechaza. Si es el emisor (y está pendiente), la cancela.
     * La solicitud de amistad se elimina de la base de datos.
     *
     * @param currentUserEmail Email del usuario que realiza la acción.
     * @param otherUserPublicId UUID público del otro usuario implicado en la solicitud (el que envió la solicitud si se rechaza,
     * o el que la recibió si se cancela).
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si no se encuentra la solicitud pendiente o alguno de los usuarios.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si el usuario actual no tiene permiso para realizar esta acción sobre la solicitud.
     */
    void declineFriendRequest(String currentUserEmail, UUID otherUserPublicId);

    /**
     * Permite al usuario actual ({@code currentUserEmail}) eliminar una amistad existente
     * con el usuario identificado por {@code friendUserPublicId}.
     * La relación de amistad se elimina de la base de datos.
     *
     * @param currentUserEmail Email del usuario que inicia la eliminación de la amistad.
     * @param friendUserPublicId UUID público del amigo a eliminar.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si no se encuentra una amistad activa con el usuario especificado o alguno de los usuarios.
     */
    void removeFriend(String currentUserEmail, UUID friendUserPublicId);

    /**
     * Obtiene la lista de amigos (relaciones con estado ACEPTADA) del usuario actual.
     *
     * @param currentUserEmail Email del usuario para el cual se recupera la lista de amigos.
     * @return Una lista de {@link FriendshipResponseDTO} representando a los amigos del usuario.
     * La lista puede estar vacía si el usuario no tiene amigos.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario actual no se encuentra.
     */
    List<FriendshipResponseDTO> getFriends(String currentUserEmail);

    /**
     * Obtiene la lista de solicitudes de amistad pendientes que el usuario actual ha recibido
     * y aún no ha respondido.
     *
     * @param currentUserEmail Email del usuario (receptor) para el cual se recuperan las solicitudes pendientes.
     * @return Una lista de {@link FriendshipResponseDTO} representando las solicitudes pendientes recibidas.
     * La lista puede estar vacía.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario actual no se encuentra.
     */
    List<FriendshipResponseDTO> getPendingRequestsReceived(String currentUserEmail);

    /**
     * Obtiene la lista de solicitudes de amistad que el usuario actual ha enviado
     * y que aún están pendientes de respuesta por parte de los destinatarios.
     *
     * @param currentUserEmail Email del usuario (emisor) para el cual se recuperan las solicitudes enviadas.
     * @return Una lista de {@link FriendshipResponseDTO} representando las solicitudes pendientes enviadas.
     * La lista puede estar vacía.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario actual no se encuentra.
     */
    List<FriendshipResponseDTO> getPendingRequestsSent(String currentUserEmail);

}