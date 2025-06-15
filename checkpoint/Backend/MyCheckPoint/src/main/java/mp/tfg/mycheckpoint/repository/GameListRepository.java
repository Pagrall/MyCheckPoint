package mp.tfg.mycheckpoint.repository;

import mp.tfg.mycheckpoint.entity.GameList;
import mp.tfg.mycheckpoint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de Spring Data JPA para la entidad {@link GameList}.
 * Proporciona métodos para gestionar las listas de juegos personalizadas de los usuarios,
 * incluyendo consultas para obtener listas por propietario, visibilidad y IDs.
 */
@Repository
public interface GameListRepository extends JpaRepository<GameList, Long> {

    /**
     * Busca una lista de juegos por su ID público (UUID).
     *
     * @param publicId El ID público de la lista de juegos.
     * @return Un {@link Optional} que contiene la {@link GameList} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<GameList> findByPublicId(UUID publicId);

    /**
     * Busca una lista de juegos por su ID interno y su propietario.
     *
     * @param internalId El ID interno de la lista de juegos.
     * @param owner El {@link User} propietario de la lista.
     * @return Un {@link Optional} que contiene la {@link GameList} si se encuentra y pertenece al propietario,
     * o un Optional vacío si no.
     */
    Optional<GameList> findByInternalIdAndOwner(Long internalId, User owner);

    /**
     * Busca una lista de juegos por su ID público (UUID) y su propietario.
     *
     * @param publicId El ID público de la lista de juegos.
     * @param owner El {@link User} propietario de la lista.
     * @return Un {@link Optional} que contiene la {@link GameList} si se encuentra y pertenece al propietario,
     * o un Optional vacío si no.
     */
    Optional<GameList> findByPublicIdAndOwner(UUID publicId, User owner);


    /**
     * Encuentra todas las listas de juegos pertenecientes a un usuario específico,
     * ordenadas por la fecha de última actualización de forma descendente.
     *
     * @param owner El {@link User} propietario de las listas.
     * @return Una lista de {@link GameList} del usuario.
     */
    List<GameList> findByOwnerOrderByUpdatedAtDesc(User owner);

    /**
     * Encuentra todas las listas de juegos que son públicas,
     * ordenadas por la fecha de última actualización de forma descendente.
     *
     * @return Una lista de todas las {@link GameList} públicas.
     */
    List<GameList> findByIsPublicTrueOrderByUpdatedAtDesc();

    /**
     * Busca una lista de juegos pública específica por su ID público (UUID).
     *
     * @param publicId El ID público de la lista de juegos.
     * @return Un {@link Optional} que contiene la {@link GameList} si es pública y se encuentra,
     * o un Optional vacío si no.
     */
    Optional<GameList> findByPublicIdAndIsPublicTrue(UUID publicId);

    /**
     * Busca una lista de juegos por su ID interno y propietario, cargando explícitamente
     * la colección de juegos asociados ({@code userGames}) para evitar problemas de N+1.
     *
     * @param listId El ID interno de la lista de juegos.
     * @param owner El {@link User} propietario de la lista.
     * @return Un {@link Optional} que contiene la {@link GameList} con sus juegos cargados si se encuentra,
     * o un Optional vacío si no.
     */
    @Query("SELECT gl FROM GameList gl LEFT JOIN FETCH gl.userGames WHERE gl.internalId = :listId AND gl.owner = :owner")
    Optional<GameList> findByIdAndOwnerWithGames(Long listId, User owner);

    /**
     * Busca una lista de juegos por su ID público y propietario, cargando explícitamente
     * la colección de juegos asociados ({@code userGames}).
     *
     * @param publicId El ID público (UUID) de la lista de juegos.
     * @param owner El {@link User} propietario de la lista.
     * @return Un {@link Optional} que contiene la {@link GameList} con sus juegos cargados si se encuentra,
     * o un Optional vacío si no.
     */
    @Query("SELECT gl FROM GameList gl LEFT JOIN FETCH gl.userGames WHERE gl.publicId = :publicId AND gl.owner = :owner")
    Optional<GameList> findByPublicIdAndOwnerWithGames(UUID publicId, User owner);

    /**
     * Busca una lista de juegos pública por su ID público, cargando explícitamente
     * la colección de juegos asociados ({@code userGames}).
     *
     * @param publicId El ID público (UUID) de la lista de juegos.
     * @return Un {@link Optional} que contiene la {@link GameList} pública con sus juegos cargados si se encuentra,
     * o un Optional vacío si no.
     */
    @Query("SELECT gl FROM GameList gl LEFT JOIN FETCH gl.userGames WHERE gl.publicId = :publicId AND gl.isPublic = true")
    Optional<GameList> findByPublicIdAndIsPublicTrueWithGames(UUID publicId);
}
