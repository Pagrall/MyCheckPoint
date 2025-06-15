package mp.tfg.mycheckpoint.service.impl;

import mp.tfg.mycheckpoint.dto.enums.FriendshipStatus;
import mp.tfg.mycheckpoint.dto.friendship.FriendshipResponseDTO;
import mp.tfg.mycheckpoint.entity.Friendship;
import mp.tfg.mycheckpoint.entity.User;
import mp.tfg.mycheckpoint.exception.DuplicateEntryException;
import mp.tfg.mycheckpoint.exception.ResourceNotFoundException;
import mp.tfg.mycheckpoint.exception.UnauthorizedOperationException;
import mp.tfg.mycheckpoint.mapper.friendship.FriendshipMapper;
import mp.tfg.mycheckpoint.repository.FriendshipRepository;
import mp.tfg.mycheckpoint.repository.UserRepository;
import mp.tfg.mycheckpoint.service.FriendshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio {@link FriendshipService} para gestionar las relaciones de amistad.
 * Proporciona la lógica de negocio para enviar, aceptar, rechazar solicitudes de amistad,
 * eliminar amigos y listar diferentes tipos de relaciones de amistad entre usuarios.
 * Todas las operaciones que modifican datos son transaccionales.
 */
@Service
public class FriendshipServiceImpl implements FriendshipService {

    private static final Logger logger = LoggerFactory.getLogger(FriendshipServiceImpl.class);

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipMapper friendshipMapper;

    /**
     * Constructor para FriendshipServiceImpl.
     * Inyecta las dependencias necesarias para la gestión de usuarios y amistades.
     *
     * @param userRepository Repositorio para acceder a los datos de los usuarios.
     * @param friendshipRepository Repositorio para acceder a los datos de las amistades.
     * @param friendshipMapper Mapper para convertir entre entidades Friendship y DTOs.
     */
    @Autowired
    public FriendshipServiceImpl(UserRepository userRepository,
                                 FriendshipRepository friendshipRepository,
                                 FriendshipMapper friendshipMapper) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendshipMapper = friendshipMapper;
    }

    /**
     * Obtiene una entidad {@link User} por su dirección de correo electrónico.
     * Lanza una {@link ResourceNotFoundException} si el usuario no se encuentra.
     *
     * @param email El email del usuario a buscar.
     * @return La entidad {@link User} encontrada.
     * @throws ResourceNotFoundException Si no se encuentra ningún usuario con el email proporcionado.
     */
    private User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    /**
     * Obtiene una entidad {@link User} por su ID público (UUID).
     * Lanza una {@link ResourceNotFoundException} si el usuario no se encuentra.
     *
     * @param publicId El ID público del usuario a buscar.
     * @return La entidad {@link User} encontrada.
     * @throws ResourceNotFoundException Si no se encuentra ningún usuario con el ID público proporcionado.
     */
    private User getUserByPublicIdOrThrow(UUID publicId) {
        return userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con Public ID: " + publicId));
    }

    /**
     * Envía una solicitud de amistad desde el usuario emisor (identificado por {@code senderUserEmail})
     * al usuario receptor (identificado por {@code receiverUserPublicId}).
     * <p>
     * Lógica de negocio:
     * <ul>
     * <li>Valida que el emisor y el receptor no sean el mismo usuario.</li>
     * <li>Comprueba si ya existe una relación (amistad aceptada o solicitud pendiente).</li>
     * <li>Si ya son amigos (ACCEPTED), lanza {@link DuplicateEntryException}.</li>
     * <li>Si el emisor ya ha enviado una solicitud pendiente al receptor, lanza {@link DuplicateEntryException}.</li>
     * <li>Si el receptor ya había enviado previamente una solicitud pendiente al emisor,
     * la solicitud original se actualiza a ACEPTADA (auto-aceptación).</li>
     * <li>Si no existen relaciones previas conflictivas, se crea una nueva solicitud con estado PENDING.</li>
     * </ul>
     *
     * @param senderUserEmail Email del usuario que envía la solicitud.
     * @param receiverUserPublicId UUID público del usuario que recibe la solicitud.
     * @return {@link FriendshipResponseDTO} representando el estado de la nueva solicitud o la amistad auto-aceptada.
     * @throws ResourceNotFoundException Si el usuario emisor o receptor no se encuentran.
     * @throws DuplicateEntryException Si ya existe una amistad o una solicitud pendiente que impide esta acción.
     * @throws IllegalArgumentException Si el emisor intenta enviarse una solicitud a sí mismo.
     */
    @Override
    @Transactional
    public FriendshipResponseDTO sendFriendRequest(String senderUserEmail, UUID receiverUserPublicId) {
        User sender = getUserByEmailOrThrow(senderUserEmail);
        User receiver = getUserByPublicIdOrThrow(receiverUserPublicId);

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("No puedes enviarte una solicitud de amistad a ti mismo.");
        }

        Optional<Friendship> existingRelationOpt = friendshipRepository.findFriendshipBetweenUsers(sender, receiver);
        if (existingRelationOpt.isPresent()) {
            Friendship relation = existingRelationOpt.get();
            if (relation.getStatus() == FriendshipStatus.ACCEPTED) {
                throw new DuplicateEntryException("Ya eres amigo de este usuario.");
            }
            if (relation.getStatus() == FriendshipStatus.PENDING) {
                if (relation.getRequester().getId().equals(sender.getId())) {
                    throw new DuplicateEntryException("Ya has enviado una solicitud de amistad a este usuario.");
                } else {
                    logger.info("Solicitud de amistad mutua detectada entre {} (ID: {}) y {} (ID: {}). Aceptando automáticamente.",
                            sender.getNombreUsuario(), sender.getId(), receiver.getNombreUsuario(), receiver.getId());
                    relation.setStatus(FriendshipStatus.ACCEPTED);
                    Friendship acceptedFriendship = friendshipRepository.save(relation);
                    return friendshipMapper.toDto(acceptedFriendship, sender);
                }
            }
        }

        Friendship newRequest = Friendship.builder()
                .requester(sender)
                .receiver(receiver)
                .status(FriendshipStatus.PENDING)
                .build();
        Friendship savedRequest = friendshipRepository.save(newRequest);
        logger.info("Usuario {} (ID: {}) envió solicitud de amistad a {} (ID: {})",
                sender.getNombreUsuario(), sender.getId(), receiver.getNombreUsuario(), receiver.getId());
        return friendshipMapper.toDto(savedRequest, sender);
    }

    /**
     * Acepta una solicitud de amistad pendiente.
     * El usuario actual (identificado por {@code currentUserEmail}) debe ser el receptor
     * de una solicitud PENDIENTE enviada por el usuario {@code requesterUserPublicId}.
     * La solicitud se actualiza al estado ACEPTADA.
     *
     * @param currentUserEmail Email del usuario que acepta la solicitud (debe ser el receptor).
     * @param requesterUserPublicId UUID público del usuario que envió originalmente la solicitud de amistad.
     * @return {@link FriendshipResponseDTO} representando la amistad ahora aceptada.
     * @throws ResourceNotFoundException Si no se encuentra una solicitud de amistad pendiente del usuario solicitante,
     * o si alguno de los usuarios no existe.
     * @throws UnauthorizedOperationException Si el {@code currentUserEmail} no corresponde al receptor de la solicitud encontrada.
     */
    @Override
    @Transactional
    public FriendshipResponseDTO acceptFriendRequest(String currentUserEmail, UUID requesterUserPublicId) {
        User currentUser = getUserByEmailOrThrow(currentUserEmail);
        User requester = getUserByPublicIdOrThrow(requesterUserPublicId);

        Friendship request = friendshipRepository.findFriendshipBetweenUsersWithStatus(requester, currentUser, FriendshipStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró una solicitud de amistad pendiente de " + requester.getNombreUsuario()));

        if (!request.getReceiver().getId().equals(currentUser.getId())) {
            logger.warn("Intento no autorizado de aceptar solicitud: Usuario {} no es el receptor de la solicitud de {}",
                    currentUser.getNombreUsuario(), requester.getNombreUsuario());
            throw new UnauthorizedOperationException("No puedes aceptar una solicitud que no te fue enviada.");
        }

        request.setStatus(FriendshipStatus.ACCEPTED);
        Friendship acceptedFriendship = friendshipRepository.save(request);
        logger.info("Usuario {} aceptó la solicitud de amistad de {}", currentUser.getNombreUsuario(), requester.getNombreUsuario());
        return friendshipMapper.toDto(acceptedFriendship, currentUser);
    }

    /**
     * Rechaza o cancela una solicitud de amistad pendiente.
     * - Si el {@code currentUserEmail} es el receptor de una solicitud PENDIENTE de {@code otherUserPublicId},
     * la solicitud se elimina (rechaza).
     * - Si el {@code currentUserEmail} es el emisor de una solicitud PENDIENTE hacia {@code otherUserPublicId},
     * la solicitud se elimina (cancela).
     *
     * @param currentUserEmail Email del usuario que realiza la acción.
     * @param otherUserPublicId UUID público del otro usuario implicado en la solicitud pendiente.
     * @throws ResourceNotFoundException Si no se encuentra una solicitud pendiente entre los usuarios especificados,
     * o si alguno de los usuarios no existe.
     * @throws UnauthorizedOperationException Si el usuario actual no es ni el emisor ni el receptor de la solicitud pendiente.
     */
    @Override
    @Transactional
    public void declineFriendRequest(String currentUserEmail, UUID otherUserPublicId) {
        User currentUser = getUserByEmailOrThrow(currentUserEmail);
        User otherUser = getUserByPublicIdOrThrow(otherUserPublicId);

        Friendship request = friendshipRepository.findFriendshipBetweenUsersWithStatus(currentUser, otherUser, FriendshipStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró una solicitud de amistad pendiente con " + otherUser.getNombreUsuario()));

        boolean isCurrentUserReceiver = request.getReceiver().getId().equals(currentUser.getId());
        boolean isCurrentUserRequester = request.getRequester().getId().equals(currentUser.getId());

        if (!isCurrentUserReceiver && !isCurrentUserRequester) {
            logger.warn("Intento no autorizado de modificar solicitud: Usuario {} no es parte de la solicitud con {}",
                    currentUser.getNombreUsuario(), otherUser.getNombreUsuario());
            throw new UnauthorizedOperationException("No tienes permiso para modificar esta solicitud de amistad.");
        }

        friendshipRepository.delete(request);
        if (isCurrentUserReceiver) {
            logger.info("Usuario {} rechazó la solicitud de amistad de {}", currentUser.getNombreUsuario(), otherUser.getNombreUsuario());
        } else { // isCurrentUserRequester
            logger.info("Usuario {} canceló su solicitud de amistad a {}", currentUser.getNombreUsuario(), otherUser.getNombreUsuario());
        }
    }

    /**
     * Elimina una amistad existente (estado ACEPTADA) entre el usuario actual y otro usuario.
     * La entidad {@link Friendship} correspondiente se elimina de la base de datos.
     *
     * @param currentUserEmail Email del usuario que inicia la acción de eliminar amigo.
     * @param friendUserPublicId UUID público del amigo que se desea eliminar.
     * @throws ResourceNotFoundException Si no existe una amistad aceptada entre los usuarios especificados,
     * o si alguno de los usuarios no existe.
     */
    @Override
    @Transactional
    public void removeFriend(String currentUserEmail, UUID friendUserPublicId) {
        User currentUser = getUserByEmailOrThrow(currentUserEmail);
        User friendToRemove = getUserByPublicIdOrThrow(friendUserPublicId);

        Friendship friendship = friendshipRepository.findFriendshipBetweenUsersWithStatus(currentUser, friendToRemove, FriendshipStatus.ACCEPTED)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró una amistad con el usuario " + friendToRemove.getNombreUsuario()));

        friendshipRepository.delete(friendship);
        logger.info("Usuario {} eliminó de sus amigos a {}", currentUser.getNombreUsuario(), friendToRemove.getNombreUsuario());
    }

    /**
     * Obtiene una lista de todos los amigos del usuario actual.
     * Se consideran amigos aquellas relaciones con estado {@link FriendshipStatus#ACCEPTED}.
     *
     * @param currentUserEmail Email del usuario para el cual se recupera la lista de amigos.
     * @return Una lista de {@link FriendshipResponseDTO}, donde cada DTO representa un amigo.
     * La lista puede estar vacía si el usuario no tiene amigos.
     * @throws ResourceNotFoundException Si el usuario actual no se encuentra.
     */
    @Override
    @Transactional(readOnly = true)
    public List<FriendshipResponseDTO> getFriends(String currentUserEmail) {
        User currentUser = getUserByEmailOrThrow(currentUserEmail);

        List<Friendship> friendships = friendshipRepository.findAcceptedFriendshipsForUser(currentUser, FriendshipStatus.ACCEPTED);
        List<FriendshipResponseDTO> responseDTOs = new ArrayList<>();

        for (Friendship friendship : friendships) {
            responseDTOs.add(friendshipMapper.toDto(friendship, currentUser));
        }

        return responseDTOs;
    }

    /**
     * Obtiene una lista de todas las solicitudes de amistad pendientes que el usuario actual ha recibido.
     *
     * @param currentUserEmail Email del usuario (receptor) para el cual se recuperan las solicitudes pendientes.
     * @return Una lista de {@link FriendshipResponseDTO}, donde cada DTO representa una solicitud recibida.
     * La lista puede estar vacía.
     * @throws ResourceNotFoundException Si el usuario actual no se encuentra.
     */
    @Override
    @Transactional(readOnly = true)
    public List<FriendshipResponseDTO> getPendingRequestsReceived(String currentUserEmail) {
        User currentUser = getUserByEmailOrThrow(currentUserEmail);

        List<Friendship> pendingRequests = friendshipRepository.findByReceiverAndStatus(currentUser, FriendshipStatus.PENDING);
        List<FriendshipResponseDTO> responseDTOs = new ArrayList<>();

        for (Friendship request : pendingRequests) {
            responseDTOs.add(friendshipMapper.toDto(request, currentUser));
        }

        return responseDTOs;
    }

    /**
     * Obtiene una lista de todas las solicitudes de amistad que el usuario actual ha enviado y que
     * aún están pendientes de respuesta por parte de los destinatarios.
     *
     * @param currentUserEmail Email del usuario (emisor) para el cual se recuperan las solicitudes enviadas.
     * @return Una lista de {@link FriendshipResponseDTO}, donde cada DTO representa una solicitud enviada.
     * La lista puede estar vacía.
     * @throws ResourceNotFoundException Si el usuario actual no se encuentra.
     */
    @Override
    @Transactional(readOnly = true)
    public List<FriendshipResponseDTO> getPendingRequestsSent(String currentUserEmail) {
        User currentUser = getUserByEmailOrThrow(currentUserEmail);

        List<Friendship> sentRequests = friendshipRepository.findByRequesterAndStatus(currentUser, FriendshipStatus.PENDING);
        List<FriendshipResponseDTO> responseDTOs = new ArrayList<>();

        for (Friendship request : sentRequests) {
            responseDTOs.add(friendshipMapper.toDto(request, currentUser));
        }

        return responseDTOs;
    }
}