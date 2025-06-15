package mp.tfg.mycheckpoint.service;

import mp.tfg.mycheckpoint.dto.usergame.GameDetailDTO;
import mp.tfg.mycheckpoint.dto.usergame.UserGameDataDTO;
import mp.tfg.mycheckpoint.dto.usergame.UserGameResponseDTO;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz para el servicio de gestión de la biblioteca de juegos personal de un usuario.
 * Define operaciones para añadir, actualizar, obtener y eliminar juegos de la biblioteca,
 * así como para obtener detalles completos de un juego, incluyendo datos específicos del usuario.
 */
public interface UserGameLibraryService {

    /**
     * Añade un nuevo juego a la biblioteca del usuario o actualiza una entrada existente.
     * Si el juego no existe en la base de datos local, se intentará obtener de IGDB y guardarlo.
     *
     * @param userEmail El email del usuario cuya biblioteca se modificará.
     * @param igdbId El ID de IGDB del juego a añadir o actualizar.
     * @param userGameDataDTO DTO con los datos específicos del usuario para este juego (estado, puntuación, etc.).
     * @return Un {@link UserGameResponseDTO} representando la entrada de la biblioteca actualizada o creada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario o el juego (en IGDB) no se encuentran.
     */
    UserGameResponseDTO addOrUpdateGameInLibrary(String userEmail, Long igdbId, UserGameDataDTO userGameDataDTO);

    /**
     * Obtiene la biblioteca completa de juegos para el usuario especificado.
     *
     * @param userEmail El email del usuario cuya biblioteca se va a recuperar.
     * @return Una lista de {@link UserGameResponseDTO}, donde cada elemento representa un juego
     * en la biblioteca del usuario con sus datos personales. Puede estar vacía.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario no se encuentra.
     */
    List<UserGameResponseDTO> getUserGameLibrary(String userEmail);

    /**
     * Obtiene los detalles de un juego específico de la biblioteca del usuario.
     *
     * @param userEmail El email del usuario.
     * @param igdbId El ID de IGDB del juego a obtener de la biblioteca.
     * @return Un {@link UserGameResponseDTO} con los datos del juego en la biblioteca del usuario.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario no se encuentra o
     * el juego no está en su biblioteca.
     */
    UserGameResponseDTO getUserGameFromLibrary(String userEmail, Long igdbId);

    /**
     * Obtiene los detalles completos de un juego, combinando información general del juego,
     * datos específicos del usuario (si está autenticado y el juego está en su biblioteca),
     * y comentarios públicos de otros usuarios.
     * Si el juego no existe localmente, se intenta obtener de IGDB para la información general.
     *
     * @param igdbId El ID de IGDB del juego para el cual se solicitan los detalles.
     * @param userEmail El email del usuario actualmente autenticado (puede ser nulo si el acceso es anónimo).
     * Si se proporciona, se intentarán cargar los datos específicos del usuario para el juego.
     * @return Un {@link GameDetailDTO} con la información completa del juego.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el juego no se encuentra (ni localmente ni en IGDB)
     * o si el {@code userEmail} es proporcionado pero el usuario no se encuentra.
     */
    GameDetailDTO getGameDetailsForUser(Long igdbId, String userEmail);

    /**
     * Obtiene la biblioteca de juegos pública de un usuario específico.
     * La visibilidad se comprueba contra el usuario actual que realiza la solicitud.
     *
     * @param publicId El ID público del usuario cuya biblioteca se va a recuperar.
     * @param currentUserEmail El email del usuario que realiza la consulta (puede ser nulo si es anónimo).
     * @return Una lista de {@link UserGameResponseDTO} representando la biblioteca pública del usuario.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario no se encuentra.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si la biblioteca del usuario no es pública o visible para el usuario actual.
     */
    List<UserGameResponseDTO> getPublicUserGameLibrary(UUID publicId, String currentUserEmail);

    /**
     * Elimina un juego de la biblioteca personal del usuario.
     *
     * @param userEmail El email del usuario.
     * @param igdbId El ID de IGDB del juego a eliminar de la biblioteca.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario no se encuentra o
     * el juego no está en su biblioteca para ser eliminado.
     */
    void removeGameFromLibrary(String userEmail, Long igdbId);
}