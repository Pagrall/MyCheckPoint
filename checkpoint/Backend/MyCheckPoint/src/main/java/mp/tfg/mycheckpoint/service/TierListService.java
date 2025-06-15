package mp.tfg.mycheckpoint.service;

import mp.tfg.mycheckpoint.dto.tierlist.*;
import java.util.List;
import java.util.UUID;

/**
 * Interfaz para el servicio de gestión de Tier Lists.
 * Define operaciones para crear, obtener, actualizar y eliminar Tier Lists,
 * así como para gestionar sus secciones (tiers) y los ítems (juegos) dentro de ellas.
 */
public interface TierListService {

    // --- Gestión General de TierLists ---

    /**
     * Crea una nueva Tier List de tipo perfil ({@link mp.tfg.mycheckpoint.dto.enums.TierListType#PROFILE_GLOBAL})
     * para el usuario especificado.
     * Incluye la creación de secciones por defecto (S, A, B, C, D y "Juegos por Clasificar").
     *
     * @param userEmail El email del usuario propietario de la Tier List.
     * @param createRequestDTO DTO con los datos para la creación (nombre, descripción, visibilidad).
     * @return {@link TierListResponseDTO} representando la Tier List recién creada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario no se encuentra.
     */
    TierListResponseDTO createProfileTierList(String userEmail, TierListCreateRequestDTO createRequestDTO);

    /**
     * Obtiene la Tier List asociada a una {@link mp.tfg.mycheckpoint.entity.GameList} específica.
     * Si no existe una Tier List para esa GameList, se crea una nueva automáticamente con secciones por defecto
     * y se sincroniza con los juegos de la GameList (añadiéndolos a la sección "Sin Clasificar").
     * El acceso se valida según la visibilidad de la GameList y si el {@code userEmail} es el propietario.
     *
     * @param userEmail El email del usuario que realiza la solicitud (puede ser nulo para acceso público).
     * Se utiliza para validar permisos si la GameList es privada.
     * @param gameListPublicId El ID público (UUID) de la GameList.
     * @return {@link TierListResponseDTO} representando la Tier List obtenida o creada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si la GameList o el usuario (si aplica) no se encuentran.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si el usuario no tiene permiso para acceder.
     */
    TierListResponseDTO getOrCreateTierListForGameList(String userEmail, UUID gameListPublicId);

    /**
     * Obtiene una Tier List específica por su ID público (UUID).
     * Si la Tier List es privada, se valida que el {@code userEmail} (si se proporciona)
     * corresponda al propietario. Las Tier Lists públicas son accesibles por cualquiera.
     *
     * @param tierListPublicId El ID público de la Tier List.
     * @param userEmail El email del usuario que realiza la solicitud (puede ser nulo para acceso público).
     * @return {@link TierListResponseDTO} de la Tier List encontrada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si la Tier List no se encuentra.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si se intenta acceder a una Tier List privada sin ser el propietario.
     */
    TierListResponseDTO getTierListByPublicId(UUID tierListPublicId, String userEmail);

    /**
     * Obtiene todas las Tier Lists de tipo perfil ({@link mp.tfg.mycheckpoint.dto.enums.TierListType#PROFILE_GLOBAL})
     * creadas por el usuario especificado.
     *
     * @param userEmail El email del usuario propietario.
     * @return Una lista de {@link TierListResponseDTO}. Puede estar vacía.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario no se encuentra.
     */
    List<TierListResponseDTO> getAllProfileTierListsForUser(String userEmail);

    /**
     * Obtiene todas las Tier Lists que han sido marcadas como públicas.
     *
     * @return Una lista de {@link TierListResponseDTO} públicas. Puede estar vacía.
     */
    List<TierListResponseDTO> getAllPublicTierLists();

    /**
     * Actualiza los metadatos (nombre, descripción, visibilidad) de una Tier List existente.
     * Solo el propietario de la Tier List puede realizar esta operación.
     *
     * @param userEmail El email del usuario propietario.
     * @param tierListPublicId El ID público de la Tier List a actualizar.
     * @param updateRequestDTO DTO con los nuevos metadatos. Solo los campos no nulos se actualizan.
     * @return {@link TierListResponseDTO} representando la Tier List actualizada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si la Tier List o el usuario no se encuentran.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si el usuario no es el propietario.
     */
    TierListResponseDTO updateTierListMetadata(String userEmail, UUID tierListPublicId, TierListUpdateRequestDTO updateRequestDTO);

    /**
     * Elimina una Tier List existente.
     * Solo el propietario de la Tier List puede realizar esta operación.
     * Esto también elimina todas sus secciones e ítems asociados.
     *
     * @param userEmail El email del usuario propietario.
     * @param tierListPublicId El ID público de la Tier List a eliminar.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si la Tier List o el usuario no se encuentran.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si el usuario no es el propietario.
     */
    void deleteTierList(String userEmail, UUID tierListPublicId);

    // --- Gestión de Secciones dentro de una TierList ---

    /**
     * Añade una nueva sección (tier) personalizada a una Tier List existente.
     * Solo el propietario puede añadir secciones. Existe un límite en la cantidad de secciones.
     *
     * @param userEmail El email del usuario propietario.
     * @param tierListPublicId El ID público de la Tier List.
     * @param sectionRequestDTO DTO con el nombre para la nueva sección.
     * @return {@link TierListResponseDTO} de la Tier List actualizada con la nueva sección.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si la Tier List o el usuario no se encuentran.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si el usuario no es el propietario.
     * @throws mp.tfg.mycheckpoint.exception.InvalidOperationException Si se alcanza el límite de secciones.
     */
    TierListResponseDTO addSectionToTierList(String userEmail, UUID tierListPublicId, TierSectionRequestDTO sectionRequestDTO);

    /**
     * Actualiza una sección (tier) específica en una Tier List.
     * Solo el propietario puede actualizar secciones. La sección por defecto "Juegos por Clasificar" no puede ser modificada.
     *
     * @param userEmail El email del usuario propietario.
     * @param tierListPublicId El ID público de la Tier List.
     * @param sectionInternalId El ID interno de la sección a actualizar.
     * @param sectionRequestDTO DTO con el nuevo nombre para la sección.
     * @return {@link TierListResponseDTO} de la Tier List actualizada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si la Tier List, la sección o el usuario no se encuentran.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si el usuario no es el propietario.
     * @throws mp.tfg.mycheckpoint.exception.InvalidOperationException Si se intenta modificar una sección no permitida.
     */
    TierListResponseDTO updateSection(String userEmail, UUID tierListPublicId, Long sectionInternalId, TierSectionRequestDTO sectionRequestDTO);

    /**
     * Elimina una sección (tier) personalizada de una Tier List.
     * Solo el propietario puede eliminar secciones. La sección por defecto "Juegos por Clasificar" no puede ser eliminada.
     * Debe quedar al menos una sección personalizable. Los ítems de la sección eliminada se mueven a "Juegos por Clasificar".
     *
     * @param userEmail El email del usuario propietario.
     * @param tierListPublicId El ID público de la Tier List.
     * @param sectionInternalId El ID interno de la sección a eliminar.
     * @return {@link TierListResponseDTO} de la Tier List actualizada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si la Tier List, la sección o el usuario no se encuentran.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si el usuario no es el propietario.
     * @throws mp.tfg.mycheckpoint.exception.InvalidOperationException Si se intenta eliminar una sección no permitida o la última sección personalizable.
     */
    TierListResponseDTO removeSectionFromTierList(String userEmail, UUID tierListPublicId, Long sectionInternalId);

    // --- Gestión de Items (Juegos) dentro de las Secciones ---

    /**
     * Añade o mueve un ítem (juego de la biblioteca del usuario) a una sección específica
     * de una Tier List de tipo {@link mp.tfg.mycheckpoint.dto.enums.TierListType#PROFILE_GLOBAL}.
     * No se puede usar para Tier Lists de tipo {@link mp.tfg.mycheckpoint.dto.enums.TierListType#FROM_GAMELIST}
     * ni para añadir a la sección "Juegos por Clasificar" (usar endpoint específico para eso).
     * Si el juego ya está en otra sección, se mueve. Se puede especificar el orden.
     *
     * @param userEmail El email del usuario propietario.
     * @param tierListPublicId El ID público de la Tier List.
     * @param sectionInternalId El ID interno de la sección destino.
     * @param itemAddRequestDTO DTO con el ID del UserGame a añadir/mover y el orden opcional.
     * @return {@link TierListResponseDTO} de la Tier List actualizada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si algún recurso no se encuentra.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si la operación no está permitida.
     * @throws mp.tfg.mycheckpoint.exception.InvalidOperationException Si la operación es inválida para el tipo de TierList o sección.
     */
    TierListResponseDTO addItemToTierListSection(String userEmail, UUID tierListPublicId, Long sectionInternalId, TierListItemAddRequestDTO itemAddRequestDTO);

    /**
     * Añade o mueve un ítem (juego de la biblioteca del usuario) directamente a la sección "Juegos por Clasificar"
     * de una Tier List de tipo {@link mp.tfg.mycheckpoint.dto.enums.TierListType#PROFILE_GLOBAL}.
     * No se puede usar para Tier Lists de tipo {@link mp.tfg.mycheckpoint.dto.enums.TierListType#FROM_GAMELIST}.
     * Si el juego ya está en otra sección, se mueve. Se puede especificar el orden.
     *
     * @param userEmail El email del usuario propietario.
     * @param tierListPublicId El ID público de la Tier List.
     * @param itemAddRequestDTO DTO con el ID del UserGame a añadir/mover y el orden opcional.
     * @return {@link TierListResponseDTO} de la Tier List actualizada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si algún recurso no se encuentra.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si la operación no está permitida.
     * @throws mp.tfg.mycheckpoint.exception.InvalidOperationException Si la operación es inválida para el tipo de TierList.
     */
    TierListResponseDTO addItemToUnclassifiedSection(String userEmail, UUID tierListPublicId, TierListItemAddRequestDTO itemAddRequestDTO);

    /**
     * Mueve un ítem existente dentro de una Tier List, permitiendo cambiar su sección y/o su orden dentro de la sección.
     * Para Tier Lists de tipo {@link mp.tfg.mycheckpoint.dto.enums.TierListType#FROM_GAMELIST}, se valida que el juego
     * del ítem aún pertenezca a la GameList origen.
     *
     * @param userEmail El email del usuario propietario.
     * @param tierListPublicId El ID público de la Tier List.
     * @param tierListItemInternalId El ID interno del TierListItem a mover.
     * @param itemMoveRequestDTO DTO con el ID de la sección destino y el nuevo orden.
     * @return {@link TierListResponseDTO} de la Tier List actualizada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si algún recurso no se encuentra.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si la operación no está permitida.
     * @throws mp.tfg.mycheckpoint.exception.InvalidOperationException Si la operación es inválida.
     */
    TierListResponseDTO moveItemInTierList(String userEmail, UUID tierListPublicId, Long tierListItemInternalId, TierListItemMoveRequestDTO itemMoveRequestDTO);

    /**
     * Elimina un ítem (juego) de una Tier List de tipo {@link mp.tfg.mycheckpoint.dto.enums.TierListType#PROFILE_GLOBAL}.
     * Esto no elimina el juego de la biblioteca general del usuario, solo de esta Tier List.
     * No se puede usar para Tier Lists de tipo {@link mp.tfg.mycheckpoint.dto.enums.TierListType#FROM_GAMELIST}.
     *
     * @param userEmail El email del usuario propietario.
     * @param tierListPublicId El ID público de la Tier List.
     * @param tierListItemInternalId El ID interno del TierListItem a eliminar.
     * @return {@link TierListResponseDTO} de la Tier List actualizada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si algún recurso no se encuentra.
     * @throws mp.tfg.mycheckpoint.exception.UnauthorizedOperationException Si la operación no está permitida.
     * @throws mp.tfg.mycheckpoint.exception.InvalidOperationException Si la operación es inválida para el tipo de TierList.
     */
    TierListResponseDTO removeItemFromTierList(String userEmail, UUID tierListPublicId, Long tierListItemInternalId);

}