package mp.tfg.mycheckpoint.repository;

import mp.tfg.mycheckpoint.dto.enums.FriendshipStatus;
import mp.tfg.mycheckpoint.entity.Friendship;
import mp.tfg.mycheckpoint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Friendship}.
 * Proporciona métodos para gestionar las relaciones de amistad y solicitudes de amistad
 * entre usuarios, incluyendo consultas personalizadas para diversos escenarios.
 */
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    /**
     * Encuentra una relación de amistad o solicitud pendiente entre dos usuarios específicos,
     * independientemente de quién fue el solicitante original.
     *
     * @param user1 El primer {@link User} en la relación.
     * @param user2 El segundo {@link User} en la relación.
     * @return Un {@link Optional} que contiene la {@link Friendship} si existe,
     * o un Optional vacío si no hay ninguna relación entre los dos usuarios.
     */
    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.requester = :user1 AND f.receiver = :user2) OR " +
            "(f.requester = :user2 AND f.receiver = :user1)")
    Optional<Friendship> findFriendshipBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

    /**
     * Encuentra una relación de amistad o solicitud entre dos usuarios con un estado específico.
     *
     * @param user1 El primer {@link User}.
     * @param user2 El segundo {@link User}.
     * @param status El {@link FriendshipStatus} deseado de la relación.
     * @return Un {@link Optional} que contiene la {@link Friendship} si se encuentra una con el estado especificado,
     * o un Optional vacío si no.
     */
    @Query("SELECT f FROM Friendship f WHERE " +
            "((f.requester = :user1 AND f.receiver = :user2) OR (f.requester = :user2 AND f.receiver = :user1)) " +
            "AND f.status = :status")
    Optional<Friendship> findFriendshipBetweenUsersWithStatus(
            @Param("user1") User user1,
            @Param("user2") User user2,
            @Param("status") FriendshipStatus status
    );

    /**
     * Encuentra todas las solicitudes de amistad pendientes que un usuario ha recibido.
     *
     * @param receiver El {@link User} que es el receptor de las solicitudes.
     * @param status El estado de la amistad, típicamente {@link FriendshipStatus#PENDING}.
     * @return Una lista de {@link Friendship} que representan las solicitudes pendientes recibidas.
     */
    List<Friendship> findByReceiverAndStatus(User receiver, FriendshipStatus status);

    /**
     * Encuentra todas las solicitudes de amistad pendientes que un usuario ha enviado.
     *
     * @param requester El {@link User} que es el solicitante.
     * @param status El estado de la amistad, típicamente {@link FriendshipStatus#PENDING}.
     * @return Una lista de {@link Friendship} que representan las solicitudes pendientes enviadas.
     */
    List<Friendship> findByRequesterAndStatus(User requester, FriendshipStatus status);

    /**
     * Encuentra todas las amistades aceptadas para un usuario específico,
     * independientemente de si el usuario fue el solicitante o el receptor.
     * Realiza un JOIN FETCH para cargar la información del solicitante y receptor.
     *
     * @param user El {@link User} para el cual se buscan las amistades.
     * @param status El estado de la amistad, típicamente {@link FriendshipStatus#ACCEPTED}.
     * @return Una lista de {@link Friendship} que representan las amistades aceptadas del usuario.
     */
    @Query("SELECT f FROM Friendship f JOIN FETCH f.requester JOIN FETCH f.receiver " +
            "WHERE (f.requester = :user OR f.receiver = :user) AND f.status = :status")
    List<Friendship> findAcceptedFriendshipsForUser(@Param("user") User user, @Param("status") FriendshipStatus status);

    /**
     * Elimina todas las relaciones de amistad (aceptadas, pendientes, etc.)
     * que involucran a un usuario específico, ya sea como solicitante o como receptor.
     * Este método es útil para la limpieza de datos cuando se elimina una cuenta de usuario.
     *
     * @param user El {@link User} cuyas relaciones de amistad serán eliminadas.
     */
    @Modifying
    @Query("DELETE FROM Friendship f WHERE f.requester = :user OR f.receiver = :user")
    void deleteAllInvolvingUser(@Param("user") User user);
}