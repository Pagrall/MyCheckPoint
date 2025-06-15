package mp.tfg.mycheckpoint.service;

import mp.tfg.mycheckpoint.dto.gameList.GameListRequestDTO;
import mp.tfg.mycheckpoint.dto.gameList.GameListResponseDTO;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz para el servicio de gestión de listas de juegos personalizadas.
 * Define operaciones CRUD para las listas de juegos, así como la gestión
 * de los juegos contenidos dentro de ellas y la visualización de listas públicas.
 */
public interface GameListService {

    /**
     * Crea una nueva lista de juegos para el usuario especificado.
     *
     * @param userEmail El email del usuario que crea la lista (propietario).
     * @param requestDTO El DTO con los datos para la creación de la lista (nombre, descripción, visibilidad).
     * @return Un {@link GameListResponseDTO} representando la lista de juegos recién creada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario propietario no se encuentra.
     */
    GameListResponseDTO createGameList(String userEmail, GameListRequestDTO requestDTO);

    /**
     * Obtiene todas las listas de juegos pertenecientes a un usuario específico.
     * Las listas se devuelven ordenadas por la fecha de última actualización de forma descendente.
     *
     * @param userEmail El email del usuario propietario de las listas.
     * @return Una lista de {@link GameListResponseDTO}. Puede estar vacía si el usuario no tiene listas.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario propietario no se encuentra.
     */
    List<GameListResponseDTO> getAllGameListsForUser(String userEmail);

    /**
     * Obtiene una lista de juegos específica por su ID público, verificando que pertenezca al usuario indicado.
     *
     * @param userEmail El email del usuario que se asume es el propietario.
     * @param listPublicId El ID público (UUID) de la lista de juegos a obtener.
     * @return Un {@link GameListResponseDTO} representando la lista de juegos encontrada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario o la lista no se encuentran,
     * o si la lista no pertenece al usuario.
     */
    GameListResponseDTO getGameListByPublicIdForUser(String userEmail, UUID listPublicId);

    /**
     * Actualiza una lista de juegos existente perteneciente al usuario especificado.
     * Solo se actualizan los campos proporcionados en el {@code requestDTO}.
     *
     * @param userEmail El email del usuario propietario de la lista.
     * @param listPublicId El ID público (UUID) de la lista de juegos a actualizar.
     * @param requestDTO El DTO con los nuevos datos para la lista.
     * @return Un {@link GameListResponseDTO} representando la lista de juegos actualizada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario o la lista no se encuentran,
     * o si la lista no pertenece al usuario.
     */
    GameListResponseDTO updateGameList(String userEmail, UUID listPublicId, GameListRequestDTO requestDTO);

    /**
     * Elimina una lista de juegos perteneciente al usuario especificado.
     * La eliminación de la lista no elimina los juegos de la biblioteca general del usuario.
     *
     * @param userEmail El email del usuario propietario de la lista.
     * @param listPublicId El ID público (UUID) de la lista de juegos a eliminar.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario o la lista no se encuentran,
     * o si la lista no pertenece al usuario.
     */
    void deleteGameList(String userEmail, UUID listPublicId);

    /**
     * Añade un juego (una entrada {@link mp.tfg.mycheckpoint.entity.UserGame}) de la biblioteca del usuario
     * a una de sus listas de juegos personalizadas.
     * El juego no se añade si ya está presente en la lista.
     *
     * @param userEmail El email del usuario propietario.
     * @param listPublicId El ID público (UUID) de la lista a la que se añadirá el juego.
     * @param userGameInternalId El ID interno de la entidad {@link mp.tfg.mycheckpoint.entity.UserGame} a añadir.
     * @return Un {@link GameListResponseDTO} representando la lista actualizada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario, la lista o la entrada UserGame no se encuentran.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si el UserGame no pertenece al propietario de la lista.
     */
    GameListResponseDTO addGameToCustomList(String userEmail, UUID listPublicId, Long userGameInternalId);

    /**
     * Elimina un juego (una entrada {@link mp.tfg.mycheckpoint.entity.UserGame}) de una lista de juegos personalizada del usuario.
     * Esto no elimina el juego de la biblioteca general del usuario, solo de esta lista particular.
     *
     * @param userEmail El email del usuario propietario.
     * @param listPublicId El ID público (UUID) de la lista de la cual se eliminará el juego.
     * @param userGameInternalId El ID interno de la entidad {@link mp.tfg.mycheckpoint.entity.UserGame} a eliminar.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario, la lista o la entrada UserGame no se encuentran.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si el UserGame no pertenece al propietario de la lista (implícito si la lista no es del usuario).
     */
    void removeGameFromCustomList(String userEmail, UUID listPublicId, Long userGameInternalId);

    /**
     * Obtiene todas las listas de juegos que han sido marcadas como públicas por sus creadores.
     * Las listas se devuelven ordenadas por la fecha de última actualización de forma descendente.
     *
     * @return Una lista de {@link GameListResponseDTO} de todas las listas públicas.
     * Puede estar vacía si no hay listas públicas.
     */
    List<GameListResponseDTO> getAllPublicGameLists();

    /**
     * Obtiene una lista de juegos pública específica por su ID público.
     *
     * @param listPublicId El ID público (UUID) de la lista de juegos pública a obtener.
     * @return Un {@link GameListResponseDTO} representando la lista pública.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si la lista con el ID especificado no se encuentra o no es pública.
     */
    GameListResponseDTO getPublicGameListByPublicId(UUID listPublicId);
}
