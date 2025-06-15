package mp.tfg.mycheckpoint.service.impl;

import mp.tfg.mycheckpoint.dto.enums.TierListType;
import mp.tfg.mycheckpoint.dto.gameList.GameListRequestDTO;
import mp.tfg.mycheckpoint.dto.gameList.GameListResponseDTO;
import mp.tfg.mycheckpoint.entity.GameList;
import mp.tfg.mycheckpoint.entity.TierList;
import mp.tfg.mycheckpoint.entity.User;
import mp.tfg.mycheckpoint.entity.UserGame;
import mp.tfg.mycheckpoint.exception.ResourceNotFoundException;
import mp.tfg.mycheckpoint.exception.UnauthorizedOperationException;
import mp.tfg.mycheckpoint.mapper.gameList.GameListMapper;
import mp.tfg.mycheckpoint.repository.GameListRepository;
import mp.tfg.mycheckpoint.repository.TierListRepository;
import mp.tfg.mycheckpoint.repository.UserGameRepository;
import mp.tfg.mycheckpoint.repository.UserRepository;
import mp.tfg.mycheckpoint.service.GameListService;
import mp.tfg.mycheckpoint.service.TierListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio {@link GameListService} para gestionar las listas
 * de juegos personalizadas de los usuarios.
 * Proporciona la lógica de negocio para crear, leer, actualizar y eliminar (CRUD)
 * listas de juegos, así como para añadir y quitar juegos de dichas listas.
 * Todas las operaciones que modifican datos son transaccionales.
 */
@Service
public class GameListServiceImpl implements GameListService {

    private static final Logger logger = LoggerFactory.getLogger(GameListServiceImpl.class);

    private final UserRepository userRepository;
    private final GameListRepository gameListRepository;
    private final UserGameRepository userGameRepository;
    private final GameListMapper gameListMapper;
    private final TierListRepository tierListRepository; // Para buscar TierLists asociadas
    private final TierListService tierListService;

    /**
     * Constructor para GameListServiceImpl.
     * Inyecta las dependencias necesarias para la gestión de listas de juegos.
     *
     * @param userRepository     Repositorio para acceder a los datos de los usuarios.
     * @param gameListRepository Repositorio para acceder a los datos de las listas de juegos.
     * @param userGameRepository Repositorio para acceder a las entradas de juegos en la biblioteca de los usuarios.
     * @param gameListMapper     Mapper para convertir entre entidades GameList y sus DTOs.
     */
    @Autowired
    public GameListServiceImpl(UserRepository userRepository,
                               GameListRepository gameListRepository,
                               UserGameRepository userGameRepository,
                               GameListMapper gameListMapper,
                               TierListRepository tierListRepository, // Inyectar
                               TierListService tierListService) {
        this.userRepository = userRepository;
        this.gameListRepository = gameListRepository;
        this.userGameRepository = userGameRepository;
        this.gameListMapper = gameListMapper;
        this.tierListRepository = tierListRepository;
        this.tierListService = tierListService;
    }

    /**
     * Obtiene una entidad {@link User} por su dirección de correo electrónico.
     * Lanza una {@link ResourceNotFoundException} si el usuario no se encuentra.
     *
     * @param email El email del usuario a buscar.
     * @return La entidad {@link User} encontrada.
     * @throws ResourceNotFoundException Si no se encuentra ningún usuario con el email proporcionado.
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Crea una nueva lista de juegos para el usuario especificado por su email.
     * El ID público de la lista se genera automáticamente.
     *
     * @param userEmail  El email del usuario que será el propietario de la nueva lista.
     * @param requestDTO El DTO que contiene los datos para la creación de la lista (nombre, descripción, visibilidad).
     * @return Un {@link GameListResponseDTO} que representa la lista de juegos recién creada.
     * @throws ResourceNotFoundException Si el usuario con el {@code userEmail} especificado no se encuentra.
     */
    @Override
    @Transactional
    public GameListResponseDTO createGameList(String userEmail, GameListRequestDTO requestDTO) {
        User owner = getUserByEmail(userEmail);
        GameList gameList = gameListMapper.toEntity(requestDTO);
        gameList.setOwner(owner);
        // gameList.setPublicId(UUID.randomUUID()); // Se genera en @PrePersist de la entidad GameList
        GameList savedGameList = gameListRepository.save(gameList);
        logger.info("User {} created GameList '{}' (Public ID: {})", userEmail, savedGameList.getName(), savedGameList.getPublicId());
        return gameListMapper.toResponseDto(savedGameList);
    }

    /**
     * Obtiene todas las listas de juegos pertenecientes a un usuario específico,
     * ordenadas por la fecha de última actualización de forma descendente.
     *
     * @param userEmail El email del usuario propietario de las listas.
     * @return Una lista de {@link GameListResponseDTO}. La lista puede estar vacía si el usuario no tiene listas.
     * @throws ResourceNotFoundException Si el usuario con el {@code userEmail} especificado no se encuentra.
     */
    @Override
    @Transactional(readOnly = true)
    public List<GameListResponseDTO> getAllGameListsForUser(String userEmail) {
        User owner = getUserByEmail(userEmail);

        List<GameList> gameLists = gameListRepository.findByOwnerOrderByUpdatedAtDesc(owner);
        List<GameListResponseDTO> responseDTOs = new ArrayList<>();

        for (GameList gameList : gameLists) {
            responseDTOs.add(gameListMapper.toResponseDto(gameList));
        }
        return responseDTOs;
    }

    /**
     * Obtiene una lista de juegos específica por su ID público (UUID), verificando que
     * pertenezca al usuario indicado por {@code userEmail}.
     * Utiliza un método del repositorio que carga la lista junto con sus juegos asociados (userGames).
     *
     * @param userEmail    El email del usuario que se asume es el propietario de la lista.
     * @param listPublicId El ID público (UUID) de la lista de juegos a obtener.
     * @return Un {@link GameListResponseDTO} representando la lista de juegos encontrada.
     * @throws ResourceNotFoundException Si el usuario no se encuentra, o si la lista de juegos
     *                                   con el {@code listPublicId} especificado no se encuentra o no pertenece al usuario.
     */
    @Override
    @Transactional(readOnly = true)
    public GameListResponseDTO getGameListByPublicIdForUser(String userEmail, UUID listPublicId) {
        User owner = getUserByEmail(userEmail);
        GameList gameList = gameListRepository.findByPublicIdAndOwnerWithGames(listPublicId, owner) // Carga con juegos
                .orElseThrow(() -> new ResourceNotFoundException(
                        "GameList with Public ID " + listPublicId + " not found for user " + userEmail));
        return gameListMapper.toResponseDto(gameList);
    }

    /**
     * Obtiene una lista de juegos pública específica por su ID público (UUID).
     * Utiliza un método del repositorio que carga la lista junto con sus juegos asociados (userGames).
     *
     * @param listPublicId El ID público (UUID) de la lista de juegos pública a obtener.
     * @return Un {@link GameListResponseDTO} representando la lista de juegos pública encontrada.
     * @throws ResourceNotFoundException Si la lista de juegos con el {@code listPublicId} especificado
     *                                   no se encuentra o no es pública.
     */
    @Override
    @Transactional(readOnly = true)
    public GameListResponseDTO getPublicGameListByPublicId(UUID listPublicId) {
        GameList gameList = gameListRepository.findByPublicIdAndIsPublicTrueWithGames(listPublicId) // Carga con juegos
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Public GameList with Public ID " + listPublicId + " not found or is not public."));
        return gameListMapper.toResponseDto(gameList);
    }

    /**
     * Obtiene todas las listas de juegos que han sido marcadas como públicas.
     * Las listas se devuelven ordenadas por la fecha de última actualización de forma descendente.
     *
     * @return Una lista de {@link GameListResponseDTO} de todas las listas públicas.
     * Puede estar vacía si no hay ninguna lista de juegos pública.
     */
    @Override
    @Transactional(readOnly = true)
    public List<GameListResponseDTO> getAllPublicGameLists() {
        List<GameList> publicGameLists = gameListRepository.findByIsPublicTrueOrderByUpdatedAtDesc();

        List<GameListResponseDTO> responseDTOs = new ArrayList<>();

        for (GameList gameList : publicGameLists) {
            responseDTOs.add(gameListMapper.toResponseDto(gameList));
        }

        return responseDTOs;
    }

    /**
     * Actualiza una lista de juegos existente perteneciente al usuario especificado.
     * Solo los campos proporcionados en el {@link GameListRequestDTO} (nombre, descripción, visibilidad)
     * serán actualizados en la entidad.
     *
     * @param userEmail    El email del usuario propietario de la lista.
     * @param listPublicId El ID público (UUID) de la lista de juegos a actualizar.
     * @param requestDTO   El DTO con los nuevos datos para la lista.
     * @return Un {@link GameListResponseDTO} representando la lista de juegos actualizada.
     * @throws ResourceNotFoundException Si el usuario no se encuentra, o si la lista de juegos
     *                                   con el {@code listPublicId} especificado no se encuentra o no pertenece al usuario.
     */
    @Override
    @Transactional
    public GameListResponseDTO updateGameList(String userEmail, UUID listPublicId, GameListRequestDTO requestDTO) {
        User owner = getUserByEmail(userEmail);
        GameList gameList = gameListRepository.findByPublicIdAndOwner(listPublicId, owner)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "GameList with Public ID " + listPublicId + " not found for user " + userEmail));

        gameListMapper.updateFromDto(requestDTO, gameList);
        GameList updatedGameList = gameListRepository.save(gameList);
        logger.info("User {} updated GameList '{}' (Public ID: {})", userEmail, updatedGameList.getName(), updatedGameList.getPublicId());
        return gameListMapper.toResponseDto(updatedGameList);
    }

    /**
     * Elimina una lista de juegos perteneciente al usuario especificado.
     * La eliminación de la lista desasocia los juegos de ella (se eliminan las entradas
     * de la tabla de unión), pero no elimina los juegos de la biblioteca general del usuario.
     * Elimina tambien la tier list asociada de tipo FROM_GAMELIST si existe,
     *
     * @param userEmail    El email del usuario propietario de la lista.
     * @param listPublicId El ID público (UUID) de la lista de juegos a eliminar.
     * @throws ResourceNotFoundException Si el usuario no se encuentra, o si la lista de juegos
     *                                   con el {@code listPublicId} especificado no se encuentra o no pertenece al usuario.
     */
    @Override
    @Transactional
    public void deleteGameList(String userEmail, UUID listPublicId) {
        User owner = getUserByEmail(userEmail);
        GameList gameListToRemove = gameListRepository.findByPublicIdAndOwner(listPublicId, owner)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "GameList con Public ID " + listPublicId + " no encontrada para el usuario " + userEmail));

        logger.info("Iniciando proceso de eliminación para GameList '{}' (Public ID: {}) por usuario {}",
                gameListToRemove.getName(), listPublicId, userEmail);

        Optional<TierList> dependentTierListOpt = tierListRepository.findBySourceGameListAndType(gameListToRemove, TierListType.FROM_GAMELIST);

        if (dependentTierListOpt.isPresent()) {
            TierList tierListToDelete = dependentTierListOpt.get();
            logger.info("Se encontró 1 TierList de tipo FROM_GAMELIST (ID: {}) que depende de GameList {}. Procediendo a eliminarla.",
                    tierListToDelete.getPublicId(), listPublicId);
            try {
                tierListService.deleteTierList(userEmail, tierListToDelete.getPublicId());
                logger.info("TierList dependiente {} eliminada.", tierListToDelete.getPublicId());
            } catch (Exception e) {
                logger.error("Error al eliminar TierList dependiente {}: {}. La eliminación de GameList se abortará para mantener la integridad.",
                        tierListToDelete.getPublicId(), e.getMessage(), e);
                throw new RuntimeException("Fallo al eliminar la TierList asociada. No se pudo eliminar la GameList.", e);
            }
        } else {
            logger.info("No se encontró ninguna TierList de tipo FROM_GAMELIST dependiente de GameList {}.", listPublicId);
        }
        try {
            gameListRepository.delete(gameListToRemove);
            logger.info("Usuario {} eliminó GameList '{}' (Public ID: {}) exitosamente.",
                    userEmail, gameListToRemove.getName(), listPublicId);
        } catch (DataIntegrityViolationException e) {
            logger.error("DataIntegrityViolationException al intentar eliminar GameList ID {}: {}. Esto podría indicar otras dependencias no manejadas o un fallo en la limpieza de TierList.",
                    listPublicId, e.getMessage());
            throw e;
        }
    }

    /**
     * Añade una entrada de juego existente en la biblioteca del usuario ({@link UserGame})
     * a una de sus listas de juegos personalizadas.
     * El juego no se añade si ya está presente en la lista para evitar duplicados.
     *
     * @param userEmail          El email del usuario propietario de la lista y del juego.
     * @param listPublicId       El ID público (UUID) de la lista de juegos a la que se añadirá el juego.
     * @param userGameInternalId El ID interno de la entidad {@link UserGame} que se desea añadir.
     * @return Un {@link GameListResponseDTO} representando la lista de juegos actualizada.
     * @throws ResourceNotFoundException      Si el usuario, la lista de juegos o la entrada UserGame no se encuentran.
     * @throws UnauthorizedOperationException Si la entrada UserGame especificada no pertenece al usuario propietario de la lista.
     */
    @Override
    @Transactional
    public GameListResponseDTO addGameToCustomList(String userEmail, UUID listPublicId, Long userGameInternalId) {
        User owner = getUserByEmail(userEmail);
        // Carga la GameList junto con sus juegos para verificar si el UserGame ya existe
        GameList gameList = gameListRepository.findByPublicIdAndOwnerWithGames(listPublicId, owner)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "GameList with Public ID " + listPublicId + " not found for user " + userEmail));

        UserGame userGame = userGameRepository.findById(userGameInternalId)
                .orElseThrow(() -> new ResourceNotFoundException("UserGame entry with ID " + userGameInternalId + " not found."));

        if (!userGame.getUser().getId().equals(owner.getId())) {
            throw new UnauthorizedOperationException(
                    "Cannot add game to list: UserGame entry ID " + userGameInternalId + " does not belong to the owner (" + userEmail + ") of the list " + listPublicId + ".");
        }

        if (gameList.getUserGames().contains(userGame)) {
            logger.warn("Game (UserGame ID: {}) is already in list '{}' (Public ID: {}). No action taken.",
                    userGameInternalId, gameList.getName(), listPublicId);
            return gameListMapper.toResponseDto(gameList); // Devuelve la lista sin cambios
        }

        gameList.getUserGames().add(userGame);
        GameList updatedGameList = gameListRepository.save(gameList);
        logger.info("Added game (UserGame ID: {}) to list '{}' (Public ID: {}) for user {}",
                userGameInternalId, updatedGameList.getName(), listPublicId, userEmail);
        return gameListMapper.toResponseDto(updatedGameList);
    }

    /**
     * Elimina una entrada de juego ({@link UserGame}) de una lista de juegos personalizada del usuario.
     * Esta operación solo elimina la asociación del juego con la lista, no elimina el juego
     * de la biblioteca general del usuario.
     *
     * @param userEmail          El email del usuario propietario de la lista.
     * @param listPublicId       El ID público (UUID) de la lista de juegos de la cual se eliminará el juego.
     * @param userGameInternalId El ID interno de la entidad {@link UserGame} a eliminar de la lista.
     * @throws ResourceNotFoundException      Si el usuario, la lista de juegos o la entrada UserGame no se encuentran.
     * @throws UnauthorizedOperationException Si la entrada UserGame no pertenece al propietario (aunque esto es implícito
     *                                        si se valida que la lista pertenece al usuario y se busca el UserGame por ID).
     */
    @Override
    @Transactional
    public void removeGameFromCustomList(String userEmail, UUID listPublicId, Long userGameInternalId) {
        User owner = getUserByEmail(userEmail);

        GameList gameList = gameListRepository.findByPublicIdAndOwnerWithGames(listPublicId, owner)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "GameList con ID público " + listPublicId + " no encontrada para el usuario " + userEmail));

        UserGame userGameToRemove = userGameRepository.findById(userGameInternalId)
                .orElseThrow(() -> new ResourceNotFoundException("UserGame entry con ID " + userGameInternalId + " no encontrado."));

        if (!userGameToRemove.getUser().getId().equals(owner.getId())) {
            throw new UnauthorizedOperationException(
                    "El UserGame ID " + userGameInternalId + " no pertenece al usuario " + userEmail +
                            " y no puede ser eliminado de la lista " + listPublicId);
        }

        boolean removed = false;
        UserGame gameFound = null;
        for (UserGame ug : gameList.getUserGames()) {
            if (ug.getInternalId().equals(userGameToRemove.getInternalId())) {
                gameFound = ug;
                break;
            }
        }

        if (gameFound != null) {
            gameList.getUserGames().remove(gameFound);
            removed = true;
        }

        if (removed) {
            gameListRepository.save(gameList);
            logger.info("Juego (UserGame ID: {}) eliminado de GameList '{}' (Public ID: {}) por usuario {}",
                    userGameInternalId, gameList.getName(), listPublicId, userEmail);

            // Sincronizar la TierList asociada
            Optional<TierList> associatedTierListOpt = tierListRepository.findBySourceGameListAndType(gameList, TierListType.FROM_GAMELIST);
            if (associatedTierListOpt.isPresent()) {
                TierList tierListToSync = associatedTierListOpt.get();
                logger.info("Iniciando sincronización para TierList ID {} (asociada a GameList ID {}) después de eliminar un juego.",
                        tierListToSync.getPublicId(), gameList.getPublicId());
                try {
                    tierListService.getOrCreateTierListForGameList(owner.getEmail(), gameList.getPublicId());
                    logger.info("Sincronización de TierList {} completada exitosamente.", tierListToSync.getPublicId());
                } catch (Exception e) {
                    logger.error("Error crítico durante la sincronización de la TierList {}: {}",
                            tierListToSync.getPublicId(), e.getMessage(), e);
                    throw new RuntimeException("Fallo al sincronizar la TierList asociada. La operación no pudo completarse.", e);
                }
            } else {
                logger.debug("No se encontró TierList asociada a GameList ID {} para sincronizar.", gameList.getPublicId());
            }

        } else {
            logger.warn("El juego (UserGame ID: {}) no se encontraba en la GameList '{}' (Public ID: {}). No se realizó ninguna acción.",
                    userGameInternalId, gameList.getName(), listPublicId);
        }
    }
}
