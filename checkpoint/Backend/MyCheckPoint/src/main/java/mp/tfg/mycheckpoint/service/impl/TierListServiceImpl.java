package mp.tfg.mycheckpoint.service.impl;

import mp.tfg.mycheckpoint.dto.enums.TierListType;
import mp.tfg.mycheckpoint.dto.tierlist.*;
import mp.tfg.mycheckpoint.entity.*;
import mp.tfg.mycheckpoint.exception.InvalidOperationException;
import mp.tfg.mycheckpoint.exception.ResourceNotFoundException;
import mp.tfg.mycheckpoint.exception.UnauthorizedOperationException;
import mp.tfg.mycheckpoint.mapper.tierlist.TierListMapper;
import mp.tfg.mycheckpoint.repository.*;
import mp.tfg.mycheckpoint.service.TierListService;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.*;

/**
 * Implementación del servicio {@link TierListService} para gestionar las operaciones
 * relacionadas con las TierLists.
 * <p>
 * Esta clase maneja la lógica de negocio para la creación, recuperación,
 * actualización y eliminación de TierLists, así como la gestión de sus
 * secciones e ítems, interactuando con la capa de persistencia.
 * </p>
 */
@Service
public class TierListServiceImpl implements TierListService {

    /**
     * Logger para esta clase.
     */
    private static final Logger logger = LoggerFactory.getLogger(TierListServiceImpl.class);
    /**
     * Número máximo de secciones personalizadas que un usuario puede definir en una TierList.
     */
    private static final int MAX_USER_DEFINED_SECTIONS = 8;
    /**
     * Número mínimo de secciones personalizadas que debe tener una TierList.
     */
    private static final int MIN_USER_DEFINED_SECTIONS = 1;
    /**
     * Nombre por defecto para la sección de juegos no clasificados.
     */
    private static final String UNCLASSIFIED_SECTION_NAME = "Juegos por Clasificar";
    /**
     * Orden por defecto para la sección de juegos no clasificados.
     */
    private static final int UNCLASSIFIED_SECTION_ORDER = 0;
    /**
     * Orden de inicio para las secciones definidas por el usuario.
     */
    private static final int USER_SECTION_START_ORDER = 1;

    private final UserRepository userRepository;
    private final GameListRepository gameListRepository;
    private final UserGameRepository userGameRepository;
    private final TierListRepository tierListRepository;
    private final TierSectionRepository tierSectionRepository;
    private final TierListItemRepository tierListItemRepository;
    private final TierListMapper tierListMapper;

    /**
     * EntityManager para interactuar con el contexto de persistencia, por ejemplo,
     * para operaciones de flush o refresh.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Constructor para la inyección de dependencias de los repositorios y mappers necesarios.
     *
     * @param userRepository         Repositorio para las operaciones de la entidad {@link User}.
     * @param gameListRepository     Repositorio para las operaciones de la entidad {@link GameList}.
     * @param userGameRepository     Repositorio para las operaciones de la entidad {@link UserGame}.
     * @param tierListRepository     Repositorio para las operaciones de la entidad {@link TierList}.
     * @param tierSectionRepository  Repositorio para las operaciones de la entidad {@link TierSection}.
     * @param tierListItemRepository Repositorio para las operaciones de la entidad {@link TierListItem}.
     * @param tierListMapper         Mapper para convertir entre entidades TierList y DTOs.
     */
    @Autowired
    public TierListServiceImpl(UserRepository userRepository,
                               GameListRepository gameListRepository,
                               UserGameRepository userGameRepository,
                               TierListRepository tierListRepository,
                               TierSectionRepository tierSectionRepository,
                               TierListItemRepository tierListItemRepository,
                               TierListMapper tierListMapper) {
        this.userRepository = userRepository;
        this.gameListRepository = gameListRepository;
        this.userGameRepository = userGameRepository;
        this.tierListRepository = tierListRepository;
        this.tierSectionRepository = tierSectionRepository;
        this.tierListItemRepository = tierListItemRepository;
        this.tierListMapper = tierListMapper;
    }

    /**
     * Obtiene un usuario por su email.
     *
     * @param email El email del usuario a buscar.
     * @return El {@link User} encontrado.
     * @throws ResourceNotFoundException si no se encuentra ningún usuario con el email proporcionado.
     */
    private User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    /**
     * Inicializa de forma perezosa las colecciones anidadas de una TierList para asegurar
     * que todos los datos necesarios estén cargados antes de mapearlos a un DTO o usarlos.
     * Esto es útil para evitar {@link org.hibernate.LazyInitializationException}.
     * Carga secciones, ítems de sección, el UserGame de cada ítem, el Game de UserGame y la Cover del Game.
     *
     * @param tierList La {@link TierList} cuyos detalles se van a inicializar. Si es null, el método no hace nada.
     */
    private void initializeTierListDetails(TierList tierList) {
        if (tierList == null) return;
        Hibernate.initialize(tierList.getSections());
        if (tierList.getSections() != null) {
            for (TierSection section : tierList.getSections()) {
                Hibernate.initialize(section.getItems());
                if (section.getItems() != null) {
                    for (TierListItem item : section.getItems()) {
                        if (item.getUserGame() != null) {
                            Hibernate.initialize(item.getUserGame());
                            if (item.getUserGame().getGame() != null) {
                                Hibernate.initialize(item.getUserGame().getGame());
                                if (item.getUserGame().getGame().getCover() != null) {
                                    Hibernate.initialize(item.getUserGame().getGame().getCover());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Busca una TierList por su ID público y propietario, inicializando sus detalles anidados.
     *
     * @param publicId El ID público de la TierList a buscar.
     * @param owner    El {@link User} propietario de la TierList.
     * @return La {@link TierList} encontrada con sus detalles (secciones, ítems, etc.) inicializados.
     * @throws ResourceNotFoundException si la TierList no se encuentra para el propietario especificado con ese ID público.
     */
    private TierList findTierListByPublicIdAndOwnerOrThrow(UUID publicId, User owner) {
        Optional<TierList> tierListOptional = tierListRepository.findByPublicIdAndOwnerWithSections(publicId, owner);

        if (tierListOptional.isPresent()) {
            TierList tierList = tierListOptional.get();
            this.initializeTierListDetails(tierList);
            return tierList;
        } else {
            throw new ResourceNotFoundException("TierList con ID público " + publicId + " no encontrada para el usuario " + owner.getNombreUsuario());
        }
    }

    /**
     * Busca una TierList por su ID público para operaciones de lectura, verificando los permisos de acceso.
     * Si la TierList es privada, solo el propietario puede acceder. Si es pública, cualquiera puede.
     * Inicializa los detalles anidados de la TierList.
     *
     * @param publicId    El ID público de la TierList a buscar.
     * @param currentUser El {@link User} que intenta acceder a la TierList. Puede ser {@code null} para accesos anónimos a TierLists públicas.
     * @return La {@link TierList} encontrada y accesible, con sus detalles inicializados.
     * @throws ResourceNotFoundException      si la TierList no se encuentra con el ID público especificado.
     * @throws UnauthorizedOperationException si el {@code currentUser} no tiene permiso para acceder a la TierList (si es privada y no es el propietario).
     */
    private TierList findTierListByPublicIdForReadOrThrow(UUID publicId, User currentUser) {
        TierList tierListBasic = tierListRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("TierList no encontrada con ID público: " + publicId));

        if (!tierListBasic.isPublic() && (currentUser == null || !Objects.equals(tierListBasic.getOwner().getId(), currentUser.getId()))) {
            throw new UnauthorizedOperationException("No tienes permiso para acceder a esta TierList.");
        }

        Optional<TierList> tierListOpt;
        if (tierListBasic.isPublic()) {
            tierListOpt = tierListRepository.findByPublicIdAndIsPublicTrueWithSections(publicId);
        } else {
            // Si no es pública, currentUser debe ser el propietario (ya validado).
            tierListOpt = tierListRepository.findByPublicIdAndOwnerWithSections(publicId, currentUser);
        }

        tierListOpt.ifPresent(this::initializeTierListDetails);
        return tierListOpt.orElseThrow(() -> new ResourceNotFoundException("TierList no encontrada o inaccesible con ID: " + publicId + ". Esto puede ocurrir si la visibilidad cambió o hay inconsistencias."));
    }


    /**
     * Reordena los ítems dentro de una lista dada, actualizando su propiedad {@code itemOrder}
     * para que sea secuencial a partir de 0.
     *
     * @param items La lista de {@link TierListItem} a reordenar. Si es null, el método no hace nada.
     */
    private void reorderItemsAndUpdate(List<TierListItem> items) {
        if (items == null) return;
        for (int i = 0; i < items.size(); i++) {
            TierListItem currentItem = items.get(i);
            if (currentItem.getItemOrder() != i) {
                currentItem.setItemOrder(i);
            }
        }
    }

    /**
     * Reordena las secciones personalizadas (no la sección por defecto "Sin Clasificar") de una TierList.
     * Asegura que el {@code sectionOrder} de estas secciones sea consecutivo,
     * comenzando desde {@code USER_SECTION_START_ORDER}.
     *
     * @param tierList La {@link TierList} cuyas secciones se van a reordenar.
     *                 Si es null o no tiene secciones, el método no hace nada.
     */
    private void reorderSections(TierList tierList) {
        if (tierList == null || tierList.getSections() == null) {
            return;
        }

        List<TierSection> userSections = new ArrayList<>();

        for (TierSection section : tierList.getSections()) {
            if (!section.isDefaultUnclassified()) {
                userSections.add(section);
            }
        }

        userSections.sort(Comparator.comparingInt(TierSection::getSectionOrder));

        for (int i = 0; i < userSections.size(); i++) {
            userSections.get(i).setSectionOrder(USER_SECTION_START_ORDER + i);
        }
    }

    /**
     * {@inheritDoc}
     * <p>Esta implementación crea una TierList de tipo {@link TierListType#PROFILE_GLOBAL}.
     * Se inicializa con un conjunto de secciones por defecto: "Juegos por Clasificar"
     * y las secciones "S", "A", "B", "C", "D".</p>
     * <p>El estado de {@code isPublic} se toma del DTO, o es {@code false} por defecto si es null.</p>
     *
     * @throws ResourceNotFoundException si el usuario con el {@code userEmail} especificado no existe.
     */
    @Override
    @Transactional
    public TierListResponseDTO createProfileTierList(String userEmail, TierListCreateRequestDTO createRequestDTO) {
        User owner = getUserByEmailOrThrow(userEmail);
        TierList tierList = TierList.builder()
                .owner(owner)
                .name(createRequestDTO.getName())
                .description(createRequestDTO.getDescription())
                .isPublic(createRequestDTO.getIsPublic() != null ? createRequestDTO.getIsPublic() : false)
                .type(TierListType.PROFILE_GLOBAL)
                .build();
        createDefaultSections(tierList);
        TierList savedTierList = tierListRepository.save(tierList);
        initializeTierListDetails(savedTierList);
        logger.info("TierList de perfil '{}' creada para el usuario {}", savedTierList.getName(), userEmail);
        return tierListMapper.toTierListResponseDTOWithSections(savedTierList);
    }

    /**
     * Crea las secciones por defecto para una nueva TierList.
     * Incluye una sección especial "Juegos por Clasificar" ({@code isDefaultUnclassified = true})
     * con orden {@code UNCLASSIFIED_SECTION_ORDER}, y un conjunto de secciones
     * personalizables ("S", "A", "B", "C", "D") con órdenes consecutivas a partir de {@code USER_SECTION_START_ORDER}.
     *
     * @param tierList La {@link TierList} a la que se añadirán las secciones por defecto.
     */
    private void createDefaultSections(TierList tierList) {
        TierSection unclassifiedSection = TierSection.builder()
                .tierList(tierList)
                .name(UNCLASSIFIED_SECTION_NAME)
                .sectionOrder(UNCLASSIFIED_SECTION_ORDER)
                .isDefaultUnclassified(true)
                .items(new ArrayList<>())
                .build();
        tierList.addSection(unclassifiedSection);

        String[] defaultNames = {"S", "A", "B", "C", "D"};
        for (int i = 0; i < defaultNames.length; i++) {
            TierSection section = TierSection.builder()
                    .tierList(tierList)
                    .name(defaultNames[i])
                    .sectionOrder(USER_SECTION_START_ORDER + i)
                    .isDefaultUnclassified(false)
                    .items(new ArrayList<>())
                    .build();
            tierList.addSection(section);
        }
    }

    /**
     * {@inheritDoc}
     * <p>Esta implementación primero busca una TierList existente de tipo {@link TierListType#FROM_GAMELIST}
     * asociada a la {@code GameList} especificada. Si existe, la sincroniza.
     * Si no existe, crea una nueva TierList, cuyo nombre, descripción y estado de publicidad
     * se basan en la {@code GameList} de origen, y luego la sincroniza.
     * El propietario de la TierList generada será el mismo propietario de la {@code GameList} de origen.
     * Se realizan verificaciones de permisos para acceder a la {@code GameList} de origen.
     *
     * @throws ResourceNotFoundException      si el usuario o la GameList no se encuentran.
     * @throws UnauthorizedOperationException si el usuario solicitante no tiene permiso para acceder a la GameList asociada.
     */
    @Override
    @Transactional
    public TierListResponseDTO getOrCreateTierListForGameList(String userEmail, UUID gameListPublicId) {
        User currentUser = getUserByEmailOrThrow(userEmail);

        Optional<GameList> sourceGameListOptional = gameListRepository.findByPublicId(gameListPublicId);
        if (sourceGameListOptional.isEmpty()) {
            throw new ResourceNotFoundException("GameList no encontrada con ID público: " + gameListPublicId);
        }
        GameList sourceGameList = sourceGameListOptional.get();

        Hibernate.initialize(sourceGameList.getUserGames());
        Hibernate.initialize(sourceGameList.getOwner());

        if (!sourceGameList.isPublic() && !Objects.equals(sourceGameList.getOwner().getId(), currentUser.getId())) {
            throw new UnauthorizedOperationException("No tienes permiso para acceder a la GameList asociada.");
        }
        User ownerOfTierList = sourceGameList.getOwner();

        Optional<TierList> existingTierListOpt = tierListRepository.findBySourceGameListAndType(sourceGameList, TierListType.FROM_GAMELIST);
        TierList tierList;

        if (existingTierListOpt.isPresent()) {
            tierList = existingTierListOpt.get();
            logger.debug("TierList existente encontrada para GameList {}. Sincronizando juegos...", gameListPublicId);
            Hibernate.initialize(tierList.getSections());

            for (TierSection section : tierList.getSections()) {
                Hibernate.initialize(section.getItems());
            }

        } else {
            logger.info("Creando nueva TierList para GameList {} ({})", sourceGameList.getName(), gameListPublicId);
            tierList = TierList.builder()
                    .owner(ownerOfTierList)
                    .name(sourceGameList.getName())
                    .description("Tier list para la lista de juegos: " + sourceGameList.getName())
                    .isPublic(sourceGameList.isPublic())
                    .type(TierListType.FROM_GAMELIST)
                    .sourceGameList(sourceGameList)
                    .build();
            createDefaultSections(tierList);
            tierList = tierListRepository.save(tierList);
        }

        synchronizeTierListWithGameList(tierList, sourceGameList);

        TierList finalTierListState = findTierListByPublicIdForReadOrThrow(tierList.getPublicId(), currentUser);
        return tierListMapper.toTierListResponseDTOWithSections(finalTierListState);
    }

    /**
     * Sincroniza los ítems de una TierList (que debe ser de tipo {@link TierListType#FROM_GAMELIST})
     * con los {@link UserGame}s presentes en su {@link GameList} de origen.
     * Esta operación realiza lo siguiente:
     * Añade a la sección "Juegos por Clasificar" de la TierList aquellos UserGames que están en la GameList pero no en la TierList.
     * Elimina de la TierList aquellos ítems cuyo UserGame asociado ya no se encuentra en la GameList de origen.
     * Solo opera si la TierList es del tipo correcto y está asociada a la GameList proporcionada.
     *
     * @param tierList La {@link TierList} a sincronizar. Debe ser de tipo {@link TierListType#FROM_GAMELIST} y estar asociada a {@code gameList}.
     * @param gameList La {@link GameList} de origen con la que se deben sincronizar los ítems.
     */
    @Transactional
    protected void synchronizeTierListWithGameList(TierList tierList, GameList gameList) {
        if (tierList.getType() != TierListType.FROM_GAMELIST || tierList.getSourceGameList() == null || !Objects.equals(tierList.getSourceGameList().getInternalId(), gameList.getInternalId())) {
            logger.warn("Intento de sincronizar TierList ID {} que no es del tipo FROM_GAMELIST o no corresponde a la GameList ID {}", tierList.getPublicId(), gameList.getPublicId());
            return;
        }
        Hibernate.initialize(tierList.getSections());
        tierList.getSections().forEach(section -> Hibernate.initialize(section.getItems()));
        Hibernate.initialize(gameList.getUserGames());

        Set<UserGame> gamesInGameList = new HashSet<>(gameList.getUserGames());
        Map<Long, TierListItem> itemsInTierListMap = new HashMap<>();
        tierList.getSections().forEach(section ->
                section.getItems().forEach(item -> {
                    if (item.getUserGame() != null) {
                        itemsInTierListMap.put(item.getUserGame().getInternalId(), item);
                    }
                })
        );

        TierSection unclassifiedSection = tierList.getSections().stream()
                .filter(TierSection::isDefaultUnclassified)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("La sección 'Sin Clasificar' no existe en la TierList " + tierList.getPublicId()));
        Hibernate.initialize(unclassifiedSection.getItems());

        boolean changesMade = false;
        for (UserGame gameFromGameList : gamesInGameList) {
            if (gameFromGameList == null || gameFromGameList.getInternalId() == null) continue;
            if (!itemsInTierListMap.containsKey(gameFromGameList.getInternalId())) {
                TierListItem newItem = TierListItem.builder()
                        .tierSection(unclassifiedSection)
                        .userGame(gameFromGameList)
                        .itemOrder(unclassifiedSection.getItems().size())
                        .build();
                unclassifiedSection.addItem(newItem);
                changesMade = true;
            }
        }

        List<TierListItem> itemsToRemoveFromTierList = new ArrayList<>();
        for (TierListItem itemInTierList : itemsInTierListMap.values()) {
            if (itemInTierList.getUserGame() == null || !gamesInGameList.contains(itemInTierList.getUserGame())) {
                itemsToRemoveFromTierList.add(itemInTierList);
            }
        }

        if (!itemsToRemoveFromTierList.isEmpty()) {
            changesMade = true;
            for (TierListItem itemToRemove : itemsToRemoveFromTierList) {
                TierSection parentSection = itemToRemove.getTierSection();
                if (parentSection != null) {
                    parentSection.removeItem(itemToRemove); // orphanRemoval debe estar configurado
                }
            }
        }

        if (changesMade) {
            tierList.getSections().forEach(section -> this.reorderItemsAndUpdate(section.getItems()));
            tierListRepository.saveAndFlush(tierList);
            logger.info("TierList {} sincronizada con GameList {}.", tierList.getPublicId(), gameList.getPublicId());
        } else {
            logger.debug("No se realizaron cambios en la TierList {} durante la sincronización con GameList {}.", tierList.getPublicId(), gameList.getPublicId());
        }
    }

    /**
     * {@inheritDoc}
     * Esta implementación verifica si la TierList es pública. Si es privada,
     * solo el propietario (identificado por {@code userEmail}) puede acceder.
     * Si {@code userEmail} es null, solo se podrán recuperar TierLists públicas.
     *
     * @throws ResourceNotFoundException      si la TierList no se encuentra o no es accesible.
     * @throws UnauthorizedOperationException si se intenta acceder a una TierList privada sin ser el propietario.
     */
    @Override
    @Transactional(readOnly = true)
    public TierListResponseDTO getTierListByPublicId(UUID tierListPublicId, String userEmail) {
        User currentUser = (userEmail != null) ? userRepository.findByEmail(userEmail).orElse(null) : null;
        TierList tierList = findTierListByPublicIdForReadOrThrow(tierListPublicId, currentUser);

        return tierListMapper.toTierListResponseDTOWithSections(tierList);
    }

    /**
     * {@inheritDoc}
     * Esta implementación recupera todas las TierLists de tipo {@link TierListType#PROFILE_GLOBAL}
     * que pertenecen al usuario especificado. Se inicializan todos los detalles anidados
     * (secciones, ítems) de cada TierList.
     *
     * @throws ResourceNotFoundException si el usuario con el {@code userEmail} especificado no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TierListResponseDTO> getAllProfileTierListsForUser(String userEmail) {
        User owner = getUserByEmailOrThrow(userEmail);

        List<TierList> tierLists = tierListRepository.findAllByOwnerAndTypeWithSections(owner);

        List<TierListResponseDTO> responseDTOs = new ArrayList<>();

        for (TierList tierList : tierLists) {
            this.initializeTierListDetails(tierList);
            responseDTOs.add(tierListMapper.toTierListResponseDTOWithSections(tierList));
        }

        return responseDTOs;
    }

    /**
     * {@inheritDoc}
     * Esta implementación recupera todas las TierLists que están marcadas como públicas.
     * Se inicializan todos los detalles anidados (secciones, ítems) de cada TierList.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TierListResponseDTO> getAllPublicTierLists() {
        List<TierList> tierLists = tierListRepository.findAllByIsPublicTrueAndFetchSections();

        List<TierListResponseDTO> responseDTOs = new ArrayList<>();

        for (TierList tierList : tierLists) {
            this.initializeTierListDetails(tierList);
            responseDTOs.add(tierListMapper.toTierListResponseDTOWithSections(tierList));
        }

        return responseDTOs;
    }

    /**
     * {@inheritDoc}
     * Esta implementación permite actualizar el nombre, la descripción y el estado de publicidad
     * de una TierList existente que pertenezca al usuario especificado.
     *
     * @throws ResourceNotFoundException si el usuario o la TierList no se encuentran para ese usuario.
     */
    @Override
    @Transactional
    public TierListResponseDTO updateTierListMetadata(String userEmail, UUID tierListPublicId, TierListUpdateRequestDTO updateRequestDTO) {
        User owner = getUserByEmailOrThrow(userEmail);

        TierList tierList = findTierListByPublicIdAndOwnerOrThrow(tierListPublicId, owner);

        tierListMapper.updateFromUpdateRequestDTO(updateRequestDTO, tierList);
        TierList updatedTierList = tierListRepository.save(tierList);
        initializeTierListDetails(updatedTierList);

        return tierListMapper.toTierListResponseDTOWithSections(updatedTierList);
    }

    /**
     * {@inheritDoc}
     * Esta implementación elimina la TierList especificada si pertenece al usuario indicado.
     * La eliminación se realiza en cascada para sus secciones e ítems, según la configuración de la entidad.
     *
     * @throws ResourceNotFoundException si el usuario o la TierList no se encuentran para ese usuario.
     */
    @Override
    @Transactional
    public void deleteTierList(String userEmail, UUID tierListPublicId) {
        User owner = getUserByEmailOrThrow(userEmail);
        TierList tierList = findTierListByPublicIdAndOwnerOrThrow(tierListPublicId, owner);
        tierListRepository.delete(tierList);
        logger.info("TierList '{}' (ID público: {}) eliminada por el usuario {}", tierList.getName(), tierListPublicId, userEmail);
    }

    /**
     * {@inheritDoc}
     * Esta implementación añade una nueva sección a la TierList. No se puede exceder el límite de
     * {@code MAX_USER_DEFINED_SECTIONS} secciones personalizadas. La nueva sección se añade
     * con un orden calculado para ser el siguiente disponible.
     *
     * @throws ResourceNotFoundException si el usuario o la TierList no se encuentran.
     * @throws InvalidOperationException si se alcanza el número máximo de secciones permitidas.
     */
    @Override
    @Transactional
    public TierListResponseDTO addSectionToTierList(String userEmail, UUID tierListPublicId, TierSectionRequestDTO sectionRequestDTO) {
        User owner = getUserByEmailOrThrow(userEmail);
        TierList tierList = findTierListByPublicIdAndOwnerOrThrow(tierListPublicId, owner);

        long currentUserDefinedSections = 0;
        for (TierSection section : tierList.getSections()) {
            if (!section.isDefaultUnclassified()) {
                currentUserDefinedSections++;
            }
        }

        if (currentUserDefinedSections >= MAX_USER_DEFINED_SECTIONS) {
            throw new InvalidOperationException("No se pueden añadir más secciones. Límite de " + MAX_USER_DEFINED_SECTIONS + " secciones personalizables alcanzado.");
        }

        int maxOrder = USER_SECTION_START_ORDER - 1;
        for (TierSection section : tierList.getSections()) {
            if (!section.isDefaultUnclassified()) {
                if (section.getSectionOrder() > maxOrder) {
                    maxOrder = section.getSectionOrder();
                }
            }
        }
        int nextOrder = maxOrder + 1;

        TierSection newSection = TierSection.builder()
                .name(sectionRequestDTO.getName())
                .sectionOrder(nextOrder)
                .isDefaultUnclassified(false)
                .items(new ArrayList<>())
                .build();
        tierList.addSection(newSection);

        TierList updatedTierList = tierListRepository.save(tierList);
        initializeTierListDetails(updatedTierList);
        return tierListMapper.toTierListResponseDTOWithSections(updatedTierList);
    }

    /**
     * {@inheritDoc}
     * Implementación específica:
     * Verifica que la sección a eliminar no sea la sección por defecto "Juegos por Clasificar".
     * Asegura que quede al menos {@code MIN_USER_DEFINED_SECTIONS} sección personalizable.
     * Si la sección a eliminar contiene ítems, estos son movidos a la sección "Juegos por Clasificar".
     * Esto implica cambiar la clave foránea de los ítems y refrescar las entidades.
     * Finalmente, se reordenan las secciones restantes y los ítems en la sección "Juegos por Clasificar".
     *
     * @throws ResourceNotFoundException si el usuario, la TierList o la sección no se encuentran.
     * @throws InvalidOperationException si se intenta eliminar la sección "Juegos por Clasificar" o si se viola el mínimo de secciones.
     * @throws IllegalStateException     si la sección "Juegos por Clasificar" no se encuentra, lo cual indica una inconsistencia de datos.
     */
    @Override
    @Transactional
    public TierListResponseDTO removeSectionFromTierList(String userEmail, UUID tierListPublicId, Long sectionInternalId) {
        User owner = getUserByEmailOrThrow(userEmail);
        //Obtener la TierList
        Optional<TierList> tierListOptional = tierListRepository.findByPublicIdAndOwnerWithSections(tierListPublicId, owner);
        if (tierListOptional.isEmpty()) {
            throw new ResourceNotFoundException("TierList con ID público " + tierListPublicId + " no encontrada para el usuario " + owner.getNombreUsuario());
        }
        TierList tierList = tierListOptional.get();

        initializeTierListDetails(tierList);

        TierSection sectionToRemove = null;
        for (TierSection section : tierList.getSections()) {
            if (section.getInternalId().equals(sectionInternalId)) {
                sectionToRemove = section;
                break;
            }
        }
        if (sectionToRemove == null) {
            throw new ResourceNotFoundException("Sección con ID " + sectionInternalId + " no encontrada en la TierList " + tierListPublicId);
        }

        if (sectionToRemove.isDefaultUnclassified()) {
            throw new InvalidOperationException("La sección 'Juegos por Clasificar' no puede ser eliminada.");
        }

        long userDefinedSectionsCount = 0;
        for (TierSection section : tierList.getSections()) {
            if (!section.isDefaultUnclassified()) {
                userDefinedSectionsCount++;
            }
        }
        if (userDefinedSectionsCount <= MIN_USER_DEFINED_SECTIONS) {
            throw new InvalidOperationException("No se puede eliminar la sección. Debe haber al menos " + MIN_USER_DEFINED_SECTIONS + " sección personalizable.");
        }

        TierSection unclassifiedSection = null;
        for (TierSection section : tierList.getSections()) {
            if (section.isDefaultUnclassified()) {
                unclassifiedSection = section;
                break;
            }
        }
        if (unclassifiedSection == null) {
            throw new IllegalStateException("La sección 'Sin Clasificar' no existe en la TierList " + tierListPublicId);
        }
        Hibernate.initialize(unclassifiedSection.getItems());

        if (!sectionToRemove.getItems().isEmpty()) {
            List<TierListItem> itemsToMove = new ArrayList<>(sectionToRemove.getItems());
            for (TierListItem item : itemsToMove) {
                item.setTierSection(unclassifiedSection);
                tierListItemRepository.save(item);
            }
            entityManager.flush();
            entityManager.refresh(unclassifiedSection);
            entityManager.refresh(sectionToRemove);
            reorderItemsAndUpdate(unclassifiedSection.getItems());
            tierSectionRepository.save(unclassifiedSection);
        }

        tierList.getSections().remove(sectionToRemove);
        reorderSections(tierList);

        TierList updatedTierList = tierListRepository.save(tierList);

        logger.info("Sección '{}' (ID: {}) eliminada de TierList. Ítems movidos (si los había). Usuario: {}",
                sectionToRemove.getName(), sectionInternalId, userEmail);

        TierList reloadedTierList = findTierListByPublicIdAndOwnerOrThrow(tierListPublicId, owner);
        return tierListMapper.toTierListResponseDTOWithSections(reloadedTierList);
    }

    /**
     * {@inheritDoc}
     * Esta implementación actualiza la sección especificada dentro de la TierList del usuario.
     *
     * @throws ResourceNotFoundException si el usuario, la TierList o la sección no se encuentran.
     */
    @Override
    @Transactional
    public TierListResponseDTO updateSection(String userEmail, UUID tierListPublicId, Long sectionInternalId, TierSectionRequestDTO sectionRequestDTO) {
        User owner = getUserByEmailOrThrow(userEmail);
        TierList tierList = findTierListByPublicIdAndOwnerOrThrow(tierListPublicId, owner);

        TierSection section = null;

        for (TierSection sect : tierList.getSections()) {
            if (Objects.equals(sect.getInternalId(), sectionInternalId)) {
                section = sect;
                break;
            }
        }

        if (section == null) {
            throw new ResourceNotFoundException("Sección con ID " + sectionInternalId + " no encontrada en la TierList " + tierListPublicId);
        }
        section.setName(sectionRequestDTO.getName());
        section.setColor(sectionRequestDTO.getColor());
        TierList updatedTierList = tierListRepository.save(tierList);
        initializeTierListDetails(updatedTierList);

        return tierListMapper.toTierListResponseDTOWithSections(updatedTierList);
    }

    /**
     * {@inheritDoc}
     * Implementación específica:
     * No se permite añadir ítems a TierLists de tipo {@link TierListType#FROM_GAMELIST} (deben gestionarse desde la GameList origen).
     * No se permite usar este metodo para añadir a la sección "Juegos por Clasificar" (usar {@link #addItemToUnclassifiedSection(String, UUID, TierListItemAddRequestDTO)}).
     * Si el {@link UserGame} ya existe en otra sección, se mueve a la sección y orden especificados.
     *
     * @throws ResourceNotFoundException      si el usuario, TierList, sección o UserGame no se encuentran.
     * @throws InvalidOperationException      si se intenta operar sobre una TierList de tipo FROM_GAMELIST, o si se intenta añadir a la sección "Juegos por Clasificar".
     * @throws UnauthorizedOperationException si el UserGame no pertenece al propietario de la TierList.
     */
    @Override
    @Transactional
    public TierListResponseDTO addItemToTierListSection(String userEmail, UUID tierListPublicId, Long sectionInternalId, TierListItemAddRequestDTO itemAddRequestDTO) {
        User owner = getUserByEmailOrThrow(userEmail);
        TierList tierList = findTierListByPublicIdAndOwnerOrThrow(tierListPublicId, owner);

        if (tierList.getType() == TierListType.FROM_GAMELIST) {
            throw new InvalidOperationException("Los juegos no se pueden añadir directamente a una TierList vinculada a una GameList. Modifica la GameList original.");
        }

        TierSection targetSection = null;

        for (TierSection section : tierList.getSections()) {
            if (Objects.equals(section.getInternalId(), sectionInternalId)) {
                targetSection = section;
                break;
            }
        }

        if (targetSection == null) {
            throw new ResourceNotFoundException("Sección con ID " + sectionInternalId + " no encontrada en la TierList " + tierListPublicId);
        }

        if (targetSection.isDefaultUnclassified()) {
            throw new InvalidOperationException("Utiliza el endpoint específico para añadir a la sección 'Juegos por Clasificar'.");
        }

        return addItemToSectionLogic(tierList, targetSection, itemAddRequestDTO.getUserGameId(), itemAddRequestDTO.getOrder(), userEmail);
    }

    /**
     * {@inheritDoc}
     * Implementación específica:
     * No se permite añadir ítems a TierLists de tipo {@link TierListType#FROM_GAMELIST} (se sincronizan automáticamente).
     * Si el {@link UserGame} ya existe en otra sección, se mueve a la sección "Juegos por Clasificar" y al orden especificado.
     *
     * @throws ResourceNotFoundException      si el usuario, TierList o UserGame no se encuentran.
     * @throws InvalidOperationException      si se intenta operar sobre una TierList de tipo FROM_GAMELIST.
     * @throws UnauthorizedOperationException si el UserGame no pertenece al propietario de la TierList.
     * @throws IllegalStateException          si la sección "Juegos por Clasificar" no existe.
     */
    @Override
    @Transactional
    public TierListResponseDTO addItemToUnclassifiedSection(String userEmail, UUID tierListPublicId, TierListItemAddRequestDTO itemAddRequestDTO) {
        User owner = getUserByEmailOrThrow(userEmail);
        TierList tierList = findTierListByPublicIdAndOwnerOrThrow(tierListPublicId, owner);

        if (tierList.getType() == TierListType.FROM_GAMELIST) {
            throw new InvalidOperationException("Los juegos se sincronizan automáticamente para TierLists de GameList. No se pueden añadir manualmente aquí.");
        }

        TierSection unclassifiedSection = null;

        for (TierSection section : tierList.getSections()) {
            if (section.isDefaultUnclassified()) {
                unclassifiedSection = section;
                break;
            }
        }

        if (unclassifiedSection == null) {
            throw new IllegalStateException("La sección 'Sin Clasificar' no existe en la TierList " + tierList.getPublicId());
        }

        return addItemToSectionLogic(tierList, unclassifiedSection, itemAddRequestDTO.getUserGameId(), itemAddRequestDTO.getOrder(), userEmail);
    }

    /**
     * Lógica interna para añadir o mover un {@link UserGame} (como {@link TierListItem}) a una sección específica
     * de una {@link TierList}.
     * Este método maneja la creación de un nuevo {@link TierListItem} si el {@link UserGame} no está presente
     * en ninguna sección de la TierList, o la reubicación y/o reordenación si ya existe.
     * Se realizan validaciones para asegurar que el {@link UserGame} pertenece al propietario de la {@link TierList}.
     * Los ítems en la sección de origen (si se mueve) y en la sección de destino son reordenados
     * después de la operación.
     *
     * @param tierList       La {@link TierList} a la que pertenece la sección de destino.
     *                       No debe ser {@code null}.
     * @param targetSection  La {@link TierSection} destino donde el ítem será añadido o movido.
     *                       No debe ser {@code null}.
     * @param userGameId     El ID del {@link UserGame} que se va a añadir o mover.
     *                       No debe ser {@code null}.
     * @param requestedOrder La posición (índice basado en cero) deseada para el ítem dentro de la
     *                       sección de destino. Si es {@code null}, menor que 0 o mayor que el
     *                       tamaño actual de la lista de ítems de la sección, el ítem se añadirá al final.
     * @param userEmail      El correo electrónico del usuario que realiza la operación. Se utiliza para
     *                       propósitos de logging. Puede ser {@code null} si el logging no es crítico
     *                       o se maneja de otra forma.
     * @return Un {@link TierListResponseDTO} que representa la {@link TierList} actualizada
     * después de añadir o mover el ítem, incluyendo todas sus secciones e ítems.
     * @throws ResourceNotFoundException      Si no se encuentra un {@link UserGame} con el {@code userGameId} proporcionado.
     * @throws UnauthorizedOperationException Si el {@link UserGame} identificado por {@code userGameId}
     *                                        no pertenece al propietario de la {@code tierList}.
     */
    private TierListResponseDTO addItemToSectionLogic(TierList tierList, TierSection targetSection, Long userGameId, Integer requestedOrder, String userEmail) {

        Optional<UserGame> userGameOptional = userGameRepository.findById(userGameId);
        if (userGameOptional.isEmpty()) {
            throw new ResourceNotFoundException("UserGame con ID " + userGameId + " no encontrado.");
        }
        UserGame userGame = userGameOptional.get();

        if (!Objects.equals(userGame.getUser().getId(), tierList.getOwner().getId())) {
            throw new UnauthorizedOperationException("El UserGame ID " + userGameId + " no pertenece al propietario de la TierList.");
        }

        Hibernate.initialize(targetSection.getItems());
        for (TierSection s : tierList.getSections()) {
            Hibernate.initialize(s.getItems());
        }

        TierListItem existingItemAnywhere = null;
        for (TierSection section : tierList.getSections()) {
            for (TierListItem item : section.getItems()) {
                if (item.getUserGame() != null && Objects.equals(item.getUserGame().getInternalId(), userGameId)) {
                    existingItemAnywhere = item;
                    break;
                }
            }
            if (existingItemAnywhere != null) {
                break;
            }
        }

        TierListItem itemToProcess;
        if (existingItemAnywhere != null) {
            itemToProcess = existingItemAnywhere;
            TierSection oldSection = itemToProcess.getTierSection();

            boolean sectionChanged = !Objects.equals(oldSection.getInternalId(), targetSection.getInternalId());
            if (sectionChanged) {
                Iterator<TierListItem> iterator = oldSection.getItems().iterator();
                while (iterator.hasNext()) {
                    TierListItem item = iterator.next();
                    if (item.getInternalId() != null && Objects.equals(item.getInternalId(), itemToProcess.getInternalId())) {
                        iterator.remove();
                        break;
                    }
                }
                reorderItemsAndUpdate(oldSection.getItems());
                itemToProcess.setTierSection(targetSection);
            }
        } else {
            itemToProcess = TierListItem.builder()
                    .userGame(userGame)
                    .tierSection(targetSection)
                    .build();
        }

        List<TierListItem> targetItems = targetSection.getItems();
        Iterator<TierListItem> targetIterator = targetItems.iterator();
        while (targetIterator.hasNext()) {
            TierListItem item = targetIterator.next();
            if (item.getInternalId() != null && Objects.equals(item.getInternalId(), itemToProcess.getInternalId())) {
                targetIterator.remove();
                break;
            }
        }

        int targetIdx = (requestedOrder == null || requestedOrder < 0 || requestedOrder > targetItems.size()) ? targetItems.size() : requestedOrder;
        targetItems.add(targetIdx, itemToProcess);
        reorderItemsAndUpdate(targetItems);

        TierList updatedTierList = tierListRepository.saveAndFlush(tierList);
        logger.info("Item (UserGame ID: {}) {} sección '{}' en TierList '{}'. Usuario: {}",
                userGameId, (existingItemAnywhere != null) ? "movido/reordenado en" : "añadido a",
                targetSection.getName(), tierList.getName(), userEmail);

        TierList reloadedTierList = findTierListByPublicIdAndOwnerOrThrow(updatedTierList.getPublicId(), tierList.getOwner());
        return tierListMapper.toTierListResponseDTOWithSections(reloadedTierList);
    }

    /**
     * Mueve un {@link TierListItem} existente dentro de una {@link TierList}, permitiendo cambiar su sección
     * y/o su orden dentro de la sección. Esta operación es la base para la funcionalidad de "arrastrar y soltar" (drag and drop).
     * El proceso se realiza de forma transaccional y sigue los siguientes pasos:
     * Valida la existencia del usuario, la TierList y el TierListItem a mover.
     * Verifica que el ítem pertenezca efectivamente a la TierList que se está modificando.
     * Localiza la sección de destino dentro de la misma TierList.
     * Desvincula el ítem de su sección original (si la sección de destino es diferente).
     * Actualiza la referencia del ítem para que apunte a la nueva sección.
     * Inserta el ítem en la lista de ítems de la sección destino en la posición (orden) especificada.
     * Reordena secuencialmente los ítems en las secciones afectadas (la original si cambió, y la de destino)
     * para mantener la consistencia del campo {@code itemOrder}.
     * Guarda la entidad raíz {@link TierList} para persistir todos los cambios en cascada.
     *
     * @param userEmail El correo electrónico del usuario propietario de la TierList que realiza la operación.
     * Utilizado para validar la propiedad.
     * @param tierListPublicId El ID público (UUID) de la {@link TierList} que contiene el ítem a mover.
     * @param tierListItemInternalId El ID interno (Long) del {@link TierListItem} que se va a mover.
     * @param itemMoveRequestDTO Un DTO {@link TierListItemMoveRequestDTO} que contiene el ID interno de la sección destino
     * ({@code targetSectionInternalId}) y el nuevo índice ({@code newOrder}) para el ítem.
     * @return Un {@link TierListResponseDTO} que representa la {@link TierList} completamente actualizada después del movimiento del ítem.
     * @throws ResourceNotFoundException si el usuario, la TierList, el TierListItem a mover o la sección destino no se encuentran con los IDs proporcionados.
     * @throws InvalidOperationException si se detecta una inconsistencia, como que el ítem a mover no pertenece a la TierList especificada.
     */
    @Override
    @Transactional
    public TierListResponseDTO moveItemInTierList(String userEmail, UUID tierListPublicId, Long tierListItemInternalId, TierListItemMoveRequestDTO itemMoveRequestDTO) {
        User owner = getUserByEmailOrThrow(userEmail);
        logger.info("INICIO moveItemInTierList - User: {}, TierListID: {}, ItemID: {}, TargetSectionID: {}, NewOrder: {}",
                userEmail, tierListPublicId, tierListItemInternalId, itemMoveRequestDTO.getTargetSectionInternalId(), itemMoveRequestDTO.getNewOrder());

        Optional<Object> tierListOptional = tierListRepository.findByPublicIdAndOwner(tierListPublicId, owner);
        if (tierListOptional.isEmpty()) {
            throw new ResourceNotFoundException("TierList con ID público " + tierListPublicId + " no encontrada para el usuario " + owner.getNombreUsuario());
        }
        TierList tierList = (TierList) tierListOptional.get();

        Optional<TierListItem> itemToMoveOptional = tierListItemRepository.findById(tierListItemInternalId);
        if (itemToMoveOptional.isEmpty()) {
            throw new ResourceNotFoundException("TierListItem con ID " + tierListItemInternalId + " no encontrado.");
        }
        TierListItem itemToMove = itemToMoveOptional.get();

        if (itemToMove.getTierSection() == null || !Objects.equals(itemToMove.getTierSection().getTierList().getInternalId(), tierList.getInternalId())) {
            throw new InvalidOperationException("El ítem no pertenece a la TierList especificada o su sección es nula.");
        }

        TierSection oldSection = itemToMove.getTierSection();

        TierSection newSection = null;
        for (TierSection section : tierList.getSections()) {
            if (Objects.equals(section.getInternalId(), itemMoveRequestDTO.getTargetSectionInternalId())) {
                newSection = section;
                break;
            }
        }
        if (newSection == null) {
            throw new ResourceNotFoundException("Sección destino con ID " + itemMoveRequestDTO.getTargetSectionInternalId() + " no encontrada en esta TierList.");
        }

        logger.debug("Item a mover (ID: {}) encontrado. Sección actual ID: {}. Sección destino ID: {}",
                itemToMove.getInternalId(), oldSection.getInternalId(), newSection.getInternalId());

        boolean sectionChanged = !Objects.equals(oldSection.getInternalId(), newSection.getInternalId());

        if (sectionChanged) {
            oldSection.getItems().remove(itemToMove);
        }

        Iterator<TierListItem> iterator = newSection.getItems().iterator();
        while (iterator.hasNext()) {
            TierListItem it = iterator.next();
            if (Objects.equals(it.getInternalId(), itemToMove.getInternalId())) {
                iterator.remove();
                break;
            }
        }

        itemToMove.setTierSection(newSection);

        List<TierListItem> targetItems = newSection.getItems();
        int targetIndex = itemMoveRequestDTO.getNewOrder();
        if (targetIndex < 0 || targetIndex > targetItems.size()) {
            targetItems.add(itemToMove);
        } else {
            targetItems.add(targetIndex, itemToMove);
        }

        // La lógica de reordenar
        if (sectionChanged) {
            reorderItemsAndUpdateOrders(oldSection);
        }
        reorderItemsAndUpdateOrders(newSection);

        TierList updatedTierList = tierListRepository.save(tierList);

        logger.info("Item ID {} movido exitosamente a sección ID {} en orden {} en TierList '{}'",
                itemToMove.getInternalId(), newSection.getInternalId(), itemMoveRequestDTO.getNewOrder(), tierList.getName());

        initializeTierListDetails(updatedTierList);
        return tierListMapper.toTierListResponseDTOWithSections(updatedTierList);
    }

    /**
     * Reordena los ítems de una sección para que su campo {@code itemOrder} sea secuencial.
     * <p>
     * Este metodo de utilidad itera sobre la lista de {@link TierListItem} de la sección proporcionada
     * y establece el {@code itemOrder} de cada ítem para que coincida con su índice actual
     * en la lista (0, 1, 2, ...). Es esencial para mantener la consistencia del orden
     * después de operaciones como añadir, eliminar o mover ítems entre secciones.
     *
     * @param section La {@link TierSection} cuyos ítems se van a reordenar. Si la sección
     * o su lista de ítems son nulas, el metodo no realiza ninguna acción.
     */
    private void reorderItemsAndUpdateOrders(TierSection section) {
        if (section == null || section.getItems() == null) return;
        List<TierListItem> items = section.getItems();
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setItemOrder(i);
        }
    }

    /**
     * Elimina un {@link TierListItem} específico de una {@link TierList}.
     * La operación se realiza de forma transaccional. Primero, se valida la existencia
     * del usuario y la TierList. Luego, se localiza el TierListItem a eliminar dentro de las
     * secciones de la TierList.
     * Si la TierList es de tipo {@link TierListType#FROM_GAMELIST}, la operación no está permitida
     * y se lanza una {@link InvalidOperationException}, ya que las modificaciones deben realizarse
     * en la GameList original.
     * Si el ítem se encuentra y la TierList no es de tipo {@code FROM_GAMELIST}:
     * Se obtiene la sección a la que pertenece el ítem.
     * Se inicializa la colección de ítems de la sección para asegurar que esté cargada.
     * Se elimina el ítem de la colección de ítems de la sección.
     * Si el ítem fue eliminado exitosamente, se reordenan los ítems restantes en esa sección.
     * Se guarda la {@link TierList} para persistir los cambios. Si la relación entre
     * {@code TierSection} y {@code TierListItem} está configurada con {@code orphanRemoval=true},
     * esto provocará la eliminación del {@code TierListItem} de la base de datos.
     * Finalmente, se recarga la TierList actualizada y se devuelve como un {@link TierListResponseDTO}.
     *
     * @param userEmail              El correo electrónico del usuario propietario de la TierList que realiza la operación.
     *                               Utilizado para identificar al usuario y validar la propiedad.
     * @param tierListPublicId       El ID público (UUID) de la {@link TierList} de la cual se eliminará el ítem.
     * @param tierListItemInternalId El ID interno (Long) del {@link TierListItem} que se va a eliminar.
     * @return Un {@link TierListResponseDTO} que representa la {@link TierList} actualizada después de la eliminación del ítem.
     * @throws ResourceNotFoundException                Si el usuario, la TierList o el TierListItem a eliminar
     *                                                  no se encuentran con los IDs proporcionados.
     * @throws InvalidOperationException                Si se intenta eliminar un ítem de una TierList de tipo
     *                                                  {@link TierListType#FROM_GAMELIST}.
     * @throws jakarta.persistence.PersistenceException Si ocurre un error durante las operaciones de persistencia
     *                                                  con la base de datos.
     */
    @Override
    @Transactional
    public TierListResponseDTO removeItemFromTierList(String userEmail, UUID tierListPublicId, Long tierListItemInternalId) {
        User owner = getUserByEmailOrThrow(userEmail);
        TierList tierList = findTierListByPublicIdAndOwnerOrThrow(tierListPublicId, owner);
        
        TierListItem itemToRemove = null;
        for (TierSection section : tierList.getSections()) {
            for (TierListItem item : section.getItems()) {
                if (Objects.equals(item.getInternalId(), tierListItemInternalId)) {
                    itemToRemove = item;
                    break;
                }
            }
            if (itemToRemove != null) {
                break;
            }
        }

        if (itemToRemove == null) {
            throw new ResourceNotFoundException("TierListItem con ID " + tierListItemInternalId + " no encontrado en la TierList " + tierListPublicId);
        }

        if (tierList.getType() == TierListType.FROM_GAMELIST) {
            throw new InvalidOperationException("Los juegos no se pueden eliminar directamente de una TierList vinculada a una GameList. Modifica la GameList original.");
        }

        TierSection section = itemToRemove.getTierSection();
        if (section != null) {
            Hibernate.initialize(section.getItems());

            boolean removed = false;
            Iterator<TierListItem> iterator = section.getItems().iterator();
            while (iterator.hasNext()) {
                TierListItem item = iterator.next();
                if (item.getInternalId() != null && Objects.equals(item.getInternalId(), itemToRemove.getInternalId())) {
                    iterator.remove();
                    removed = true;
                    break;
                }
            }

            if (removed) {
                reorderItemsAndUpdateOrders(section); // Reordenar los ítems restantes
            }
        } else {
            logger.warn("El ítem ID {} a eliminar no tenía una sección asociada.", tierListItemInternalId);
        }

        TierList updatedTierList = tierListRepository.saveAndFlush(tierList);
        logger.info("Item ID {} (UserGame ID: {}) eliminado de TierList '{}' por usuario {}",
                itemToRemove.getInternalId(), (itemToRemove.getUserGame() != null ? itemToRemove.getUserGame().getInternalId() : "N/A"), tierList.getName(), userEmail);

        TierList reloadedTierList = findTierListByPublicIdAndOwnerOrThrow(updatedTierList.getPublicId(), owner);
        return tierListMapper.toTierListResponseDTOWithSections(reloadedTierList);
    }
}