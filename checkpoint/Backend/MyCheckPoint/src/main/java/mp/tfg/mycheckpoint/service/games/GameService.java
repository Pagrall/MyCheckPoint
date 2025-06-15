package mp.tfg.mycheckpoint.service.games;


import jakarta.transaction.Transactional;
import mp.tfg.mycheckpoint.dto.enums.GameType;
import mp.tfg.mycheckpoint.dto.enums.ReleaseStatus;
import mp.tfg.mycheckpoint.dto.games.*;
import mp.tfg.mycheckpoint.entity.games.*;
import mp.tfg.mycheckpoint.mapper.games.*;
import mp.tfg.mycheckpoint.repository.games.*;
import org.hibernate.Hibernate; // IMPORTANTE
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Servicio para la gestión de la información de los videojuegos en la base de datos local.
 * Se encarga de la lógica de negocio para crear, actualizar y recuperar entidades {@link Game}
 * y sus relaciones asociadas (géneros, plataformas, compañías, etc.).
 * Este servicio es crucial para mantener la consistencia y la integridad de los datos
 * de los juegos, especialmente al sincronizar información de fuentes externas como IGDB.
 */
@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;
    private final GameModeRepository gameModeRepository;
    private final GenreRepository genreRepository;
    private final FranchiseRepository franchiseRepository;
    private final GameEngineRepository gameEngineRepository;
    private final KeywordRepository keywordRepository;
    private final PlatformRepository platformRepository;
    private final ThemeRepository themeRepository;
    private final CompanyRepository companyRepository;
    private final GameCompanyInvolvementRepository gameCompanyInvolvementRepository;

    private final GameMapper gameMapper;
    private final GameModeMapper gameModeMapper;
    private final GenreMapper genreMapper;
    private final FranchiseMapper franchiseMapper;
    private final GameEngineMapper gameEngineMapper;
    private final KeywordMapper keywordMapper;
    private final PlatformMapper platformMapper;
    private final PlatformLogoMapper platformLogoMapper;
    private final ThemeMapper themeMapper;
    private final CompanyMapper companyMapper;
    private final InvolvedCompanyMapper involvedCompanyMapper;

    private final CoverMapper coverMapper;
    private final ArtworkMapper artworkMapper;
    private final ScreenshotMapper screenshotMapper;
    private final VideoMapper videoMapper;
    private final WebsiteMapper websiteMapper;


    /**
     * Constructor para {@code GameService}.
     * Inyecta todos los repositorios y mappers necesarios para la gestión de entidades de juegos.
     *
     * @param gameRepository Repositorio para la entidad {@link Game}.
     * @param gameModeRepository Repositorio para la entidad {@link GameMode}.
     * @param genreRepository Repositorio para la entidad {@link Genre}.
     * @param franchiseRepository Repositorio para la entidad {@link Franchise}.
     * @param gameEngineRepository Repositorio para la entidad {@link GameEngine}.
     * @param keywordRepository Repositorio para la entidad {@link Keyword}.
     * @param platformRepository Repositorio para la entidad {@link Platform}.
     * @param themeRepository Repositorio para la entidad {@link Theme}.
     * @param companyRepository Repositorio para la entidad {@link Company}.
     * @param gameCompanyInvolvementRepository Repositorio para la entidad {@link GameCompanyInvolvement}.
     * @param gameMapper Mapper para {@link Game} y {@link GameDto}.
     * @param gameModeMapper Mapper para {@link GameMode} y {@link GameModeDto}.
     * @param genreMapper Mapper para {@link Genre} y {@link GenreDto}.
     * @param franchiseMapper Mapper para {@link Franchise} y {@link FranchiseDto}.
     * @param gameEngineMapper Mapper para {@link GameEngine} y {@link GameEngineDto}.
     * @param keywordMapper Mapper para {@link Keyword} y {@link KeywordDto}.
     * @param platformMapper Mapper para {@link Platform} y {@link PlatformDto}.
     * @param platformLogoMapper Mapper para {@link PlatformLogo} y {@link PlatformLogoDto}.
     * @param themeMapper Mapper para {@link Theme} y {@link ThemeDto}.
     * @param companyMapper Mapper para {@link Company} y {@link CompanyInfoDto}.
     * @param involvedCompanyMapper Mapper para {@link GameCompanyInvolvement} y {@link InvolvedCompanyDto}.
     * @param coverMapper Mapper para {@link Cover} y {@link CoverDto}.
     * @param artworkMapper Mapper para {@link Artwork} y {@link ArtworkDto}.
     * @param screenshotMapper Mapper para {@link Screenshot} y {@link ScreenshotDto}.
     * @param videoMapper Mapper para {@link Video} y {@link VideoDto}.
     * @param websiteMapper Mapper para {@link Website} y {@link WebsiteDto}.
     */
    @Autowired
    public GameService(GameRepository gameRepository, GameModeRepository gameModeRepository,
                       GenreRepository genreRepository, FranchiseRepository franchiseRepository,
                       GameEngineRepository gameEngineRepository, KeywordRepository keywordRepository,
                       PlatformRepository platformRepository,PlatformLogoMapper platformLogoMapper, ThemeRepository themeRepository,
                       CompanyRepository companyRepository, GameCompanyInvolvementRepository gameCompanyInvolvementRepository,
                       GameMapper gameMapper, GameModeMapper gameModeMapper, GenreMapper genreMapper,
                       FranchiseMapper franchiseMapper, GameEngineMapper gameEngineMapper,
                       KeywordMapper keywordMapper, PlatformMapper platformMapper, ThemeMapper themeMapper,
                       CompanyMapper companyMapper, InvolvedCompanyMapper involvedCompanyMapper,
                       CoverMapper coverMapper, ArtworkMapper artworkMapper, ScreenshotMapper screenshotMapper,
                       VideoMapper videoMapper, WebsiteMapper websiteMapper) {
        this.gameRepository = gameRepository;
        this.gameModeRepository = gameModeRepository;
        this.genreRepository = genreRepository;
        this.franchiseRepository = franchiseRepository;
        this.gameEngineRepository = gameEngineRepository;
        this.keywordRepository = keywordRepository;
        this.platformRepository = platformRepository;
        this.themeRepository = themeRepository;
        this.companyRepository = companyRepository;
        this.gameCompanyInvolvementRepository = gameCompanyInvolvementRepository;
        this.gameMapper = gameMapper;
        this.gameModeMapper = gameModeMapper;
        this.genreMapper = genreMapper;
        this.franchiseMapper = franchiseMapper;
        this.gameEngineMapper = gameEngineMapper;
        this.keywordMapper = keywordMapper;
        this.platformMapper = platformMapper;
        this.platformLogoMapper = platformLogoMapper;
        this.themeMapper = themeMapper;
        this.companyMapper = companyMapper;
        this.involvedCompanyMapper = involvedCompanyMapper;
        this.coverMapper = coverMapper;
        this.artworkMapper = artworkMapper;
        this.screenshotMapper = screenshotMapper;
        this.videoMapper = videoMapper;
        this.websiteMapper = websiteMapper;
    }

    /**
     * Guarda o actualiza una lista de juegos en la base de datos local.
     * Itera sobre la lista de {@link GameDto} proporcionada y procesa cada uno
     * utilizando {@link #processSingleGameDto(GameDto, Game)}.
     *
     * @param gameDtos La lista de DTOs de juegos a procesar.
     * @return Una lista de entidades {@link Game} que han sido persistidas o actualizadas.
     * La lista puede estar vacía si la entrada es nula o no contiene DTOs válidos.
     */
    @Transactional
    public List<Game> saveGames(List<GameDto> gameDtos) {
        List<Game> processedGames = new ArrayList<>();
        if (gameDtos == null) return processedGames;
        for (GameDto gameDto : gameDtos) {
            if (gameDto != null && gameDto.getIgdbId() != null) {
                Game processedGameEntity = processSingleGameDto(gameDto, null);
                if (processedGameEntity != null) {
                    processedGames.add(processedGameEntity);
                } else {
                    logger.warn("processSingleGameDto devolvió null para GameDto IGDB ID: {}", gameDto.getIgdbId());
                }
            } else if (gameDto != null) {
                logger.warn("GameDto con IGDB ID nulo: {}", gameDto.getName());
            } else {
                logger.warn("GameDto nulo en la lista.");
            }
        }
        return processedGames;
    }

    /**
     * Procesa un único {@link GameDto} para crear o actualizar una entidad {@link Game} y sus relaciones.
     * Este método orquesta la lógica de encontrar o crear la entidad base del juego,
     * procesar sus relaciones jerárquicas (padre, versión), sus colecciones ManyToMany
     * (géneros, plataformas, etc.), compañías involucradas y listas de juegos relacionados
     * (DLCs, expansiones, remakes, etc.).
     *
     * @param gameDto El DTO del juego a procesar.
     * @param prospectiveParentGameEntity La entidad del juego padre prospectivo, si aplica (por ejemplo, al procesar un DLC).
     * Puede ser {@code null}.
     * @return La entidad {@link Game} persistida y completamente procesada, o {@code null} si el {@code gameDto} es inválido.
     * @throws RuntimeException Si ocurre un error durante el proceso de guardado en la base de datos.
     */
    private Game processSingleGameDto(GameDto gameDto, Game prospectiveParentGameEntity) {
        if (gameDto == null || gameDto.getIgdbId() == null) {
            logger.warn("processSingleGameDto: GameDto nulo o sin IgdbId. Saltando.");
            return null;
        }

        logger.debug("Inicio procesando GameDto: ID={}, Name={}, isFullDetails={}, ProspectiveParentIGDBID={}",
                gameDto.getIgdbId(), gameDto.getName(), gameDto.isFullDetails(),
                (prospectiveParentGameEntity != null ? prospectiveParentGameEntity.getIgdbId() : "null"));

        Game currentGameEntity = findOrCreateAndUpdateBaseGame(gameDto, prospectiveParentGameEntity);
        if (currentGameEntity == null) {
            logger.error("Error: findOrCreateBaseGame devolvió null para GameDto IGDB ID: {}", gameDto.getIgdbId());
            return null;
        }

        try {
            currentGameEntity = gameRepository.save(currentGameEntity);
        } catch (Exception e) {
            logger.error("Error guardando la entidad base del juego (IGDB ID: {}) : {}", gameDto.getIgdbId(), e.getMessage(), e);
            throw e;
        }
        final Game managedGameEntity = currentGameEntity;

        processParentAndVersionRelationships(gameDto, managedGameEntity);

        if (gameDto.isFullDetails()) {
            logger.debug("DTO completo (ID: {}), procesando colecciones detalladas.", managedGameEntity.getIgdbId());
            processAssociatedManyToManyCollections(gameDto, managedGameEntity);
            processInvolvedCompanies(gameDto, managedGameEntity);
        } else {
            logger.debug("DTO parcial (ID: {}), se omite procesamiento de colecciones detalladas.", managedGameEntity.getIgdbId());
        }

        Game savedMainGameEntity;
        try {
            savedMainGameEntity = gameRepository.save(managedGameEntity);
        } catch (Exception e) {
            logger.error("Error guardando managedGameEntity (IGDB ID: {}) después de procesar colecciones: {}", gameDto.getIgdbId(), e.getMessage(), e);
            throw e;
        }

        processChildGameLists(gameDto, savedMainGameEntity);
        processRelatedGameLists(gameDto, savedMainGameEntity); // Incluye remakes, remasters, similar_games

        Game finalSavedEntity;
        try {
            finalSavedEntity = gameRepository.save(savedMainGameEntity);
        } catch (Exception e) {
            logger.error("Error en el guardado final del juego (IGDB ID: {}): {}",
                    (savedMainGameEntity != null ? savedMainGameEntity.getIgdbId() : gameDto.getIgdbId()),
                    e.getMessage(), e);
            throw e;
        }

        if (finalSavedEntity != null) {
            initializeLazyCollections(finalSavedEntity);
        }

        logger.debug("Fin procesando GameDto: ID={}", gameDto.getIgdbId());
        return finalSavedEntity;
    }

    /**
     * Encuentra una entidad {@link Game} existente por su IGDB ID o crea una nueva si no existe.
     * Luego, actualiza los campos base de la entidad (existente o nueva) con la información del {@link GameDto}.
     * Maneja la lógica de actualización basada en si el DTO entrante se considera "completo"
     * ({@code gameDto.isFullDetails()}) o parcial.
     *
     * @param gameDto El DTO que contiene la información del juego.
     * @param prospectiveParentGameEntity El juego padre prospectivo, si este juego es un DLC/expansión. Puede ser {@code null}.
     * @return La entidad {@link Game} (existente actualizada o nueva creada) lista para procesamiento adicional de relaciones.
     */
    private Game findOrCreateAndUpdateBaseGame(GameDto gameDto, Game prospectiveParentGameEntity) {
        Optional<Game> existingGameOptional = gameRepository.findByIgdbId(gameDto.getIgdbId());

        Game gameToProcess; // Esta será la entidad que se devolverá, ya sea existente o nueva.

        if (existingGameOptional.isPresent()) {
            Game existingGame = existingGameOptional.get();
            logger.debug("Juego existente IGDB ID: {}. Actualizando. isFullDetails DTO: {}, isFullDetails Entidad: {}",
                    gameDto.getIgdbId(), gameDto.isFullDetails(), existingGame.isFullDetails());

            if (gameDto.isFullDetails()) {
                logger.debug("GameDto (ID: {}) es completo. Actualización completa.", gameDto.getIgdbId());
                gameMapper.updateFromDto(gameDto, existingGame); // MapStruct actualiza 'existingGame' en el sitio.

                // Si la entidad existente no estaba marcada como completa, y el DTO sí lo está,
                // actualizamos la marca en la entidad.
                if (!existingGame.isFullDetails()) {
                    existingGame.setFullDetails(true);
                    logger.debug("Entidad Game (ID: {}) marcada como isFullDetails = true.", existingGame.getIgdbId());
                }
            } else {
                logger.debug("GameDto (ID: {}) es parcial. Actualización selectiva.", gameDto.getIgdbId());
                updateSelectiveFields(gameDto, existingGame);
            }
            setFirstReleaseStatusFromDto(gameDto, existingGame);
            if (prospectiveParentGameEntity != null &&
                    (existingGame.getParentGame() == null ||
                            !existingGame.getParentGame().getIgdbId().equals(prospectiveParentGameEntity.getIgdbId()))) {
                existingGame.setParentGame(prospectiveParentGameEntity);
                logger.debug("Asignado prospectiveParentGameEntity (ID: {}) al juego existente (ID: {}).",
                        prospectiveParentGameEntity.getIgdbId(), existingGame.getIgdbId());
            }
            gameToProcess = existingGame;
        } else {
            logger.debug("Creando nuevo juego para IGDB ID: {}", gameDto.getIgdbId());
            Game newGame = gameMapper.toEntity(gameDto); // Mapea los campos básicos del DTO a una nueva entidad.

            // Establece el estado de 'isFullDetails' de la nueva entidad basado en el DTO.
            logger.debug("Nueva entidad Game (ID: {}) creada con isFullDetails = {} (si el campo existe).",
                    newGame.getIgdbId(), gameDto.isFullDetails());

            // Establece el estado de lanzamiento (ReleaseStatus) para el nuevo juego.
            setFirstReleaseStatusFromDto(gameDto, newGame);
            if (prospectiveParentGameEntity != null) {
                newGame.setParentGame(prospectiveParentGameEntity);
                logger.debug("Asignado prospectiveParentGameEntity (ID: {}) al nuevo juego (ID: {}).",
                        prospectiveParentGameEntity.getIgdbId(), newGame.getIgdbId());
            }
            gameToProcess = newGame;
        }

        return gameToProcess;
    }

    /**
     * Procesa y establece las relaciones jerárquicas de un juego, como su juego padre
     * (si es un DLC o expansión) y su juego versión padre (si es una edición diferente del mismo juego base).
     * Llama recursivamente a {@link #processSingleGameDto(GameDto, Game)} para asegurar que las entidades
     * padre/versión existan en la base de datos antes de establecer la relación.
     *
     * @param gameDto El DTO del juego actual, que puede contener información sobre sus padres.
     * @param managedGameEntity La entidad {@link Game} gestionada para el juego actual.
     */
    private void processParentAndVersionRelationships(GameDto gameDto, Game managedGameEntity) {
        if (gameDto.getParentGameInfo() != null && managedGameEntity.getParentGame() == null) {
            DlcInfoDto parentDto = gameDto.getParentGameInfo();
            if (parentDto.getIgdbId() != null && !parentDto.getIgdbId().equals(managedGameEntity.getIgdbId())) {
                GameDto parentAsGameDto = convertDlcInfoToGameDto(parentDto);
                if (parentAsGameDto != null) {
                    Game parentEntity = processSingleGameDto(parentAsGameDto, null);
                    if (parentEntity != null) {
                        managedGameEntity.setParentGame(parentEntity);
                    }
                }
            }
        }

        if (gameDto.getVersionParent() != null && managedGameEntity.getVersionParentGame() == null) {
            DlcInfoDto vpDto = gameDto.getVersionParent();
            if (vpDto.getIgdbId() != null && !vpDto.getIgdbId().equals(managedGameEntity.getIgdbId())) {
                GameDto vpAsGameDto = convertDlcInfoToGameDto(vpDto);
                if (vpAsGameDto != null) {
                    Game vpEntity = processSingleGameDto(vpAsGameDto, null);
                    if (vpEntity != null) {
                        managedGameEntity.setVersionParentGame(vpEntity);
                    }
                }
            }
        }
    }

    /**
     * Procesa las listas de juegos hijos (DLCs, expansiones, bundles) especificadas en el {@link GameDto}.
     * Para cada juego hijo, lo procesa (crea o actualiza) y establece la relación con el juego padre.
     *
     * @param gameDto El DTO del juego padre.
     * @param parentGame La entidad {@link Game} del juego padre.
     */
    private void processChildGameLists(GameDto gameDto, Game parentGame) {
        processChildGameList(gameDto.getDlcs(), parentGame, "DLCs");
        processChildGameList(gameDto.getExpansions(), parentGame, "Expansions");
        processChildGameList(gameDto.getBundles(), parentGame, "Bundles");
    }

    /**
     * Procesa las listas de juegos relacionados (remakes, remasters, juegos similares) especificadas en el {@link GameDto}.
     * Para cada juego relacionado, lo procesa (crea o actualiza) y establece la relación ManyToMany con el juego principal.
     *
     * @param gameDto El DTO del juego principal.
     * @param mainGame La entidad {@link Game} del juego principal.
     */
    private void processRelatedGameLists(GameDto gameDto, Game mainGame) {
        if (gameDto.getRemakes() != null) {
            List<GameDto> remakesAsGameDtos = new ArrayList<>();
            for (DlcInfoDto dlcInfo : gameDto.getRemakes()) {
                GameDto convertedDto = this.convertDlcInfoToGameDto(dlcInfo);
                if (convertedDto != null) {
                    remakesAsGameDtos.add(convertedDto);
                }
            }
            updateRelatedGameCollection(
                    remakesAsGameDtos,
                    mainGame,
                    mainGame.getRemakeVersions(),
                    "Remakes"
            );
        }

        if (gameDto.getRemasters() != null) {
            List<GameDto> remastersAsGameDtos = new ArrayList<>();
            for (DlcInfoDto dlcInfo : gameDto.getRemasters()) {
                GameDto convertedDto = this.convertDlcInfoToGameDto(dlcInfo);
                if (convertedDto != null) {
                    remastersAsGameDtos.add(convertedDto);
                }
            }
            updateRelatedGameCollection(
                    remastersAsGameDtos,
                    mainGame,
                    mainGame.getRemasterVersions(),
                    "Remasters"
            );
        }

        if (gameDto.getSimilarGames() != null) {
            List<GameDto> similarGamesAsGameDtos = new ArrayList<>();
            for (SimilarGameInfoDto similarInfo : gameDto.getSimilarGames()) {
                GameDto convertedDto = this.convertSimilarGameInfoToGameDto(similarInfo);
                if (convertedDto != null) {
                    similarGamesAsGameDtos.add(convertedDto);
                }
            }
            updateRelatedGameCollection(
                    similarGamesAsGameDtos,
                    mainGame,
                    mainGame.getSimilarGames(),
                    "SimilarGames"
            );
        }
    }

    /**
     * Método de utilidad para actualizar una colección de juegos relacionados (ej. remakes, remasters, juegos similares).
     * Compara la colección existente en la entidad con la nueva colección derivada del DTO y aplica los cambios
     * necesarios (añadir, eliminar) para sincronizarlas.
     *
     * @param relatedGameDtos Lista de DTOs de los juegos relacionados.
     * @param mainGame La entidad {@link Game} principal.
     * @param existingRelatedCollection La colección {@link Set} existente de juegos relacionados en la entidad principal.
     * @param relationName Nombre descriptivo de la relación (para logging).
     */
    // Método helper para actualizar colecciones de juegos relacionados (remakes, remasters, similar)
    private void updateRelatedGameCollection(List<GameDto> relatedGameDtos, Game mainGame, Set<Game> existingRelatedCollection, String relationName) {
        logger.debug("Procesando lista de {} para el juego principal ID: {}", relationName, mainGame.getIgdbId());
        Set<Game> newRelatedEntities = new HashSet<>();

        for (GameDto relatedDto : relatedGameDtos) {
            if (relatedDto.getIgdbId() != null && !relatedDto.getIgdbId().equals(mainGame.getIgdbId())) {
                Game relatedEntity = processSingleGameDto(relatedDto, null);
                if (relatedEntity != null) {
                    newRelatedEntities.add(relatedEntity);
                }
            }
        }

        if (!existingRelatedCollection.equals(newRelatedEntities)) {
            logger.debug("Colección de '{}' para el juego {} ha cambiado. Actualizando.", relationName, mainGame.getIgdbId());
            existingRelatedCollection.clear();
            existingRelatedCollection.addAll(newRelatedEntities);
        }
    }

    /**
     * Inicializa explícitamente las colecciones cargadas de forma perezosa (LAZY) de una entidad {@link Game}.
     * Esto es útil para asegurar que todas las colecciones necesarias estén disponibles
     * después de que la entidad se haya desvinculado de la sesión de Hibernate (por ejemplo, antes de devolverla desde el servicio).
     *
     * @param game La entidad {@link Game} cuyas colecciones se inicializarán. Si es {@code null}, el método no hace nada.
     */
    private void initializeLazyCollections(Game game) {
        if (game == null) {
            return;
        }

        logger.debug("Inicializando colecciones LAZY para Game ID: {}", game.getIgdbId());

        Hibernate.initialize(game.getChildGames());
        Hibernate.initialize(game.getSimilarGames());
        Hibernate.initialize(game.getRemakeVersions());
        Hibernate.initialize(game.getRemasterVersions());
        Hibernate.initialize(game.getInvolvedCompanies());
        Hibernate.initialize(game.getGameModes());
        Hibernate.initialize(game.getGenres());
        Hibernate.initialize(game.getThemes());
        Hibernate.initialize(game.getKeywords());
        Hibernate.initialize(game.getPlatforms());
        Hibernate.initialize(game.getGameEngines());
        Hibernate.initialize(game.getFranchises());
        Hibernate.initialize(game.getArtworks());
        Hibernate.initialize(game.getScreenshots());
        Hibernate.initialize(game.getWebsites());
        Hibernate.initialize(game.getVideos());
    }

    /**
     * Actualiza selectivamente los campos de una entidad {@link Game} con la información de un {@link GameDto}.
     * Este método se utiliza cuando el DTO entrante es parcial ({@code gameDto.isFullDetails() == false}),
     * actualizando solo los campos básicos y las colecciones de elementos embebidos directos
     * (artworks, screenshots, videos, websites) si el DTO los proporciona y no están vacíos.
     * No modifica relaciones ManyToMany complejas ni el estado {@code isFullDetails} de la entidad.
     *
     * @param dto El {@link GameDto} con los datos de actualización.
     * @param entity La entidad {@link Game} a actualizar.
     */
    private void updateSelectiveFields(GameDto dto, Game entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getSlug() != null) {
            entity.setSlug(dto.getSlug());
        }
        if (dto.getCover() != null) {
            entity.setCover(coverMapper.toEntity(dto.getCover()));
        }
        if (dto.getTotalRating() != null) {
            entity.setTotalRating(dto.getTotalRating());
        }
        if (dto.getTotalRatingCount() != null) {
            entity.setTotalRatingCount(dto.getTotalRatingCount());
        }
        if (dto.getGameType() != null) {
            entity.setGameType(dto.getGameType());
        }
        if (dto.getFirstReleaseDate() != null) {
            entity.setFirstReleaseDate(gameMapper.mapTimestampToInstant(dto.getFirstReleaseDate()));
        }

        if (dto.getSummary() != null) {
            if (!dto.getSummary().isEmpty() || entity.getSummary() == null || entity.getSummary().isEmpty()) {
                entity.setSummary(dto.getSummary());
            }
        }
        if (dto.getStoryline() != null) {
            if (!dto.getStoryline().isEmpty() || entity.getStoryline() == null || entity.getStoryline().isEmpty()) {
                entity.setStoryline(dto.getStoryline());
            }
        }

        if (dto.getArtworks() != null && !dto.getArtworks().isEmpty()) {
            List<Artwork> newArtworks = new ArrayList<>();
            for (ArtworkDto artworkDto : dto.getArtworks()) {
                newArtworks.add(artworkMapper.toEntity(artworkDto));
            }
            entity.getArtworks().clear();
            entity.getArtworks().addAll(newArtworks);
        }

        if (dto.getScreenshots() != null && !dto.getScreenshots().isEmpty()) {
            List<Screenshot> newScreenshots = new ArrayList<>();
            for (ScreenshotDto screenshotDto : dto.getScreenshots()) {
                newScreenshots.add(screenshotMapper.toEntity(screenshotDto));
            }
            entity.getScreenshots().clear();
            entity.getScreenshots().addAll(newScreenshots);
        }

        if (dto.getVideos() != null && !dto.getVideos().isEmpty()) {
            List<Video> newVideos = new ArrayList<>();
            for (VideoDto videoDto : dto.getVideos()) {
                newVideos.add(videoMapper.toEntity(videoDto));
            }
            entity.getVideos().clear();
            entity.getVideos().addAll(newVideos);
        }

        if (dto.getWebsites() != null && !dto.getWebsites().isEmpty()) {
            List<Website> newWebsites = new ArrayList<>();
            for (WebsiteDto websiteDto : dto.getWebsites()) {
                newWebsites.add(websiteMapper.toEntity(websiteDto));
            }
            entity.getWebsites().clear();
            entity.getWebsites().addAll(newWebsites);
        }
    }

    /**
     * Establece el campo {@code firstReleaseStatus} de una entidad {@link Game}
     * basándose en el {@code gameStatus.id} del {@link GameDto}.
     * Si el DTO no proporciona un estado o si el DTO es completo pero no tiene estado,
     * se establece como {@link ReleaseStatus#UNKNOWN}.
     *
     * @param gameDto El DTO del juego.
     * @param gameEntity La entidad del juego a modificar.
     */
    private void setFirstReleaseStatusFromDto(GameDto gameDto, Game gameEntity) {
        if (gameDto.getGameStatus() != null && gameDto.getGameStatus().getId() != null) {
            Integer statusId = gameDto.getGameStatus().getId();
            ReleaseStatus statusEnum = ReleaseStatus.mapFromIgdbValue(statusId);
            if (gameEntity.getFirstReleaseStatus() != statusEnum) {
                gameEntity.setFirstReleaseStatus(statusEnum);
                logger.debug("Establecido firstReleaseStatus: {} (IGDB game_status.id: {}) para IGDB ID {}", statusEnum, statusId, gameDto.getIgdbId());
            }
            gameDto.setFirstReleaseStatus(statusEnum); // Asegura que el DTO también refleje el enum
        } else if (gameEntity.getFirstReleaseStatus() == null || (gameDto.isFullDetails() && gameDto.getGameStatus() == null)) {
            if (gameEntity.getFirstReleaseStatus() != ReleaseStatus.UNKNOWN) {
                gameEntity.setFirstReleaseStatus(ReleaseStatus.UNKNOWN);
                logger.debug("IGDB game_status nulo o no presente en DTO (fullDetails: {}). FirstReleaseStatus puesto a UNKNOWN para IGDB ID {}.", gameDto.isFullDetails(), gameDto.getIgdbId());
            }
            gameDto.setFirstReleaseStatus(ReleaseStatus.UNKNOWN);
        }
    }

    /**
     * Procesa una lista de DTOs de juegos hijos (como DLCs, expansiones) y los asocia
     * con una entidad de juego padre.
     * Llama recursivamente a {@link #processSingleGameDto(GameDto, Game)} para cada hijo.
     *
     * @param dlcInfoList Lista de {@link DlcInfoDto} representando los juegos hijos.
     * @param parentGame La entidad {@link Game} del juego padre.
     * @param listType Nombre descriptivo del tipo de lista de hijos (para logging).
     */
    private void processChildGameList(List<DlcInfoDto> dlcInfoList, Game parentGame, String listType) {
        if (dlcInfoList != null && parentGame != null) {
            logger.debug("Procesando lista de {} para el juego padre ID: {}", listType, parentGame.getIgdbId());

            for (DlcInfoDto dlcInfo : dlcInfoList) {
                if (dlcInfo != null && dlcInfo.getIgdbId() != null && !dlcInfo.getIgdbId().equals(parentGame.getIgdbId())) {
                    GameDto childGameDto = convertDlcInfoToGameDto(dlcInfo);
                    if (childGameDto != null) {
                        processSingleGameDto(childGameDto, parentGame);
                    }
                }
            }
        }
    }

    /**
     * Convierte un {@link DlcInfoDto} (información resumida de un juego relacionado)
     * a un {@link GameDto} más completo, marcándolo como parcial ({@code isFullDetails = false}).
     * Inicializa las colecciones vacías en el {@code GameDto}.
     *
     * @param dlcInfo El {@link DlcInfoDto} a convertir.
     * @return Un {@link GameDto} o {@code null} si el {@code dlcInfo} es inválido.
     */

    private GameDto convertDlcInfoToGameDto(DlcInfoDto dlcInfo) {
        if (dlcInfo == null || dlcInfo.getIgdbId() == null) return null;
        GameDto gameDto = new GameDto();
        gameDto.setIgdbId(dlcInfo.getIgdbId());
        gameDto.setName(dlcInfo.getName());
        gameDto.setCover(dlcInfo.getCover());
        gameDto.setTotalRating(dlcInfo.getTotalRating());
        gameDto.setGameType(dlcInfo.getGameType());
        gameDto.setSlug(dlcInfo.getSlug());
        initializeEmptyCollections(gameDto);
        gameDto.setFullDetails(false); // Marca como parcial ya que viene de un DTO resumido
        return gameDto;
    }

    /**
     * Convierte un {@link SimilarGameInfoDto} (información resumida de un juego similar)
     * a un {@link GameDto} más completo, marcándolo como parcial ({@code isFullDetails = false})
     * y estableciendo el tipo de juego por defecto a {@link GameType#GAME}.
     * Inicializa las colecciones vacías en el {@code GameDto}.
     *
     * @param similarInfo El {@link SimilarGameInfoDto} a convertir.
     * @return Un {@link GameDto} o {@code null} si el {@code similarInfo} es inválido.
     */
    private GameDto convertSimilarGameInfoToGameDto(SimilarGameInfoDto similarInfo) {
        if (similarInfo == null || similarInfo.getIgdbId() == null) return null;
        GameDto gameDto = new GameDto();
        gameDto.setIgdbId(similarInfo.getIgdbId());
        gameDto.setName(similarInfo.getName());
        gameDto.setSlug(similarInfo.getSlug());
        gameDto.setSummary(similarInfo.getSummary());
        gameDto.setCover(similarInfo.getCover());
        gameDto.setTotalRating(similarInfo.getTotalRating());
        gameDto.setGameType(GameType.GAME);
        initializeEmptyCollections(gameDto);
        gameDto.setFullDetails(false); // Marca como parcial
        return gameDto;
    }

    /**
     * Inicializa todas las colecciones en un {@link GameDto} a listas vacías si son {@code null}.
     * Esto previene {@link NullPointerException} al intentar añadir elementos a colecciones no inicializadas.
     *
     * @param gameDto El {@link GameDto} cuyas colecciones se inicializarán. Si es {@code null}, el método no hace nada.
     */
    private void initializeEmptyCollections(GameDto gameDto) {
        if (gameDto == null) return;
        if (gameDto.getGameModes() == null) gameDto.setGameModes(new ArrayList<>());
        if (gameDto.getGenres() == null) gameDto.setGenres(new ArrayList<>());
        if (gameDto.getArtworks() == null) gameDto.setArtworks(new ArrayList<>());
        if (gameDto.getFranchises() == null) gameDto.setFranchises(new ArrayList<>());
        if (gameDto.getGameEngines() == null) gameDto.setGameEngines(new ArrayList<>());
        if (gameDto.getKeywords() == null) gameDto.setKeywords(new ArrayList<>());
        if (gameDto.getPlatforms() == null) gameDto.setPlatforms(new ArrayList<>());
        if (gameDto.getScreenshots() == null) gameDto.setScreenshots(new ArrayList<>());
        if (gameDto.getWebsites() == null) gameDto.setWebsites(new ArrayList<>());
        if (gameDto.getVideos() == null) gameDto.setVideos(new ArrayList<>());
        if (gameDto.getThemes() == null) gameDto.setThemes(new ArrayList<>());
        if (gameDto.getDlcs() == null) gameDto.setDlcs(new ArrayList<>());
        if (gameDto.getExpansions() == null) gameDto.setExpansions(new ArrayList<>());
        if (gameDto.getBundles() == null) gameDto.setBundles(new ArrayList<>());
        if (gameDto.getRemakes() == null) gameDto.setRemakes(new ArrayList<>());
        if (gameDto.getRemasters() == null) gameDto.setRemasters(new ArrayList<>());
        if (gameDto.getSimilarGames() == null) gameDto.setSimilarGames(new ArrayList<>());
        if (gameDto.getInvolvedCompanies() == null) gameDto.setInvolvedCompanies(new ArrayList<>());
    }

    /**
     * Obtiene o crea una entidad {@link GameMode} persistida.
     * Si la entidad ya existe (por IGDB ID), la devuelve. Si no, la crea, la guarda y la devuelve.
     * Actualiza el nombre si es diferente en el DTO.
     *
     * @param dto El {@link GameModeDto} con la información del modo de juego.
     * @return La entidad {@link GameMode} persistida, o {@code null} si el DTO es inválido.
     */
    public GameMode getOrCreateGameMode(GameModeDto dto) {
        if (dto == null || dto.getIgdbId() == null) {
            return null;
        }

        Optional<GameMode> existingGameModeOptional = gameModeRepository.findByIgdbId(dto.getIgdbId());

        if (existingGameModeOptional.isPresent()) {
            GameMode existingEntity = existingGameModeOptional.get();

            if (!Objects.equals(dto.getName(), existingEntity.getName())) {
                existingEntity.setName(dto.getName());
                return gameModeRepository.save(existingEntity);
            } else {
                return existingEntity;
            }
        } else {
            GameMode newGameMode = gameModeMapper.toEntity(dto);
            return gameModeRepository.save(newGameMode);
        }
    }

    /**
     * Obtiene o crea una entidad {@link Genre} persistida.
     * Si la entidad ya existe (por IGDB ID), la devuelve. Si no, la crea, la guarda y la devuelve.
     * Actualiza el nombre si es diferente en el DTO.
     *
     * @param dto El {@link GenreDto} con la información del género.
     * @return La entidad {@link Genre} persistida, o {@code null} si el DTO es inválido.
     */
    public Genre getOrCreateGenre(GenreDto dto) {
        if (dto == null || dto.getIgdbId() == null) {
            return null;
        }

        Optional<Genre> existingGenreOptional = genreRepository.findByIgdbId(dto.getIgdbId());

        if (existingGenreOptional.isPresent()) {
            Genre existingEntity = existingGenreOptional.get();

            if (!Objects.equals(dto.getName(), existingEntity.getName())) {
                existingEntity.setName(dto.getName());
                return genreRepository.save(existingEntity);
            } else {
                return existingEntity;
            }
        } else {
            Genre newGenre = genreMapper.toEntity(dto);
            return genreRepository.save(newGenre);
        }
    }

    /**
     * Obtiene o crea una entidad {@link Franchise} persistida.
     * Si la entidad ya existe (por IGDB ID), la devuelve. Si no, la crea, la guarda y la devuelve.
     * Actualiza el nombre si es diferente en el DTO.
     *
     * @param dto El {@link FranchiseDto} con la información de la franquicia.
     * @return La entidad {@link Franchise} persistida, o {@code null} si el DTO es inválido.
     */
    public Franchise getOrCreateFranchise(FranchiseDto dto) {
        if (dto == null || dto.getIgdbId() == null) {
            return null;
        }

        Optional<Franchise> existingFranchiseOptional = franchiseRepository.findByIgdbId(dto.getIgdbId());

        if (existingFranchiseOptional.isPresent()) {
            Franchise existingEntity = existingFranchiseOptional.get();

            if (!Objects.equals(dto.getName(), existingEntity.getName())) {
                existingEntity.setName(dto.getName());
                return franchiseRepository.save(existingEntity);
            } else {
                return existingEntity;
            }

        } else {
            Franchise newFranchise = franchiseMapper.toEntity(dto);
            return franchiseRepository.save(newFranchise);
        }
    }

    /**
     * Obtiene o crea una entidad {@link GameEngine} persistida.
     * Si la entidad ya existe (por IGDB ID), la devuelve. Si no, la crea, la guarda y la devuelve.
     * Actualiza el nombre si es diferente en el DTO.
     *
     * @param dto El {@link GameEngineDto} con la información del motor de juego.
     * @return La entidad {@link GameEngine} persistida, o {@code null} si el DTO es inválido.
     */
    public GameEngine getOrCreateGameEngine(GameEngineDto dto) {
        if (dto == null || dto.getIgdbId() == null) {
            return null;
        }

        Optional<GameEngine> existingEngineOptional = gameEngineRepository.findByIgdbId(dto.getIgdbId());

        if (existingEngineOptional.isPresent()) {
            GameEngine existingEntity = existingEngineOptional.get();

            if (!Objects.equals(dto.getName(), existingEntity.getName())) {
                existingEntity.setName(dto.getName());
                return gameEngineRepository.save(existingEntity);
            } else {
                return existingEntity;
            }

        } else {
            GameEngine newEngine = gameEngineMapper.toEntity(dto);
            return gameEngineRepository.save(newEngine);
        }
    }

    /**
     * Obtiene o crea una entidad {@link Keyword} persistida.
     * Si la entidad ya existe (por IGDB ID), la devuelve. Si no, la crea, la guarda y la devuelve.
     * Actualiza el nombre si es diferente en el DTO.
     *
     * @param dto El {@link KeywordDto} con la información de la palabra clave.
     * @return La entidad {@link Keyword} persistida, o {@code null} si el DTO es inválido.
     */
    public Keyword getOrCreateKeyword(KeywordDto dto) {
        if (dto == null || dto.getIgdbId() == null) {
            return null;
        }

        Optional<Keyword> existingKeywordOptional = keywordRepository.findByIgdbId(dto.getIgdbId());

        if (existingKeywordOptional.isPresent()) {
            Keyword existingEntity = existingKeywordOptional.get();

            if (!Objects.equals(dto.getName(), existingEntity.getName())) {
                existingEntity.setName(dto.getName());
                return keywordRepository.save(existingEntity);
            } else {
                return existingEntity;
            }

        } else {
            Keyword newKeyword = keywordMapper.toEntity(dto);
            return keywordRepository.save(newKeyword);
        }
    }

    /**
     * Obtiene o crea una entidad {@link Platform} persistida.
     * Si la entidad ya existe (por IGDB ID), la devuelve. Si no, la crea, la guarda y la devuelve.
     * Actualiza los campos relevantes (nombre, nombre alternativo, logo) si son diferentes en el DTO.
     *
     * @param dto El {@link PlatformDto} con la información de la plataforma.
     * @return La entidad {@link Platform} persistida, o {@code null} si el DTO es inválido.
     */
    public Platform getOrCreatePlatform(PlatformDto dto) {
        if (dto == null || dto.getIgdbId() == null) {
            return null;
        }

        Optional<Platform> existingPlatformOptional = platformRepository.findByIgdbId(dto.getIgdbId());

        // 3. Estructura condicional principal para manejar los dos casos.
        if (existingPlatformOptional.isPresent()) {
            Platform existingPlatform = existingPlatformOptional.get();
            boolean hasChanges = false; // Bandera para controlar si es necesario guardar.

            if (!Objects.equals(dto.getName(), existingPlatform.getName())) {
                existingPlatform.setName(dto.getName());
                hasChanges = true;
            }

            if (!Objects.equals(dto.getAlternativeName(), existingPlatform.getAlternativeName())) {
                existingPlatform.setAlternativeName(dto.getAlternativeName());
                hasChanges = true;
            }

            PlatformLogoDto logoDto = dto.getPlatformLogo();
            PlatformLogo currentLogoEntity = existingPlatform.getPlatformLogo();

            if (logoDto != null) {
                PlatformLogo newLogoEntity = platformLogoMapper.toEntity(logoDto);
                if (currentLogoEntity == null || !currentLogoEntity.equals(newLogoEntity)) {
                    existingPlatform.setPlatformLogo(newLogoEntity);
                    hasChanges = true;
                }
            } else if (currentLogoEntity != null) {
                existingPlatform.setPlatformLogo(null);
                hasChanges = true;
            }

            if (hasChanges) {
                return platformRepository.save(existingPlatform);
            } else {
                return existingPlatform;
            }

        } else {
            Platform newPlatform = platformMapper.toEntity(dto);
            return platformRepository.save(newPlatform);
        }
    }

    /**
     * Procesa las colecciones ManyToMany asociadas a una entidad {@link Game}
     * (modos de juego, géneros, franquicias, motores, palabras clave, plataformas, temas).
     * Para cada colección en el {@link GameDto}, obtiene o crea las entidades relacionadas
     * y actualiza la colección correspondiente en la entidad {@link Game} si ha habido cambios.
     * Si el DTO no proporciona una colección (es {@code null}), se limpia la colección existente en la entidad.
     *
     * @param gameDto El DTO del juego con las colecciones a procesar.
     * @param gameEntity La entidad {@link Game} cuyas colecciones se actualizarán.
     */
    private void processAssociatedManyToManyCollections(GameDto gameDto, Game gameEntity) {
        logger.debug("Procesando colecciones ManyToMany para el juego completo {}", gameEntity.getIgdbId());

        // --- Sincronización de GameModes ---
        if (gameDto.getGameModes() != null) {
            Set<GameMode> gameModesFromDto = new HashSet<>();
            // Bucle estructurado para procesar la colección
            for (GameModeDto dto : gameDto.getGameModes()) {
                GameMode entity = this.getOrCreateGameMode(dto);
                if (entity != null) {
                    gameModesFromDto.add(entity);
                }
            }
            if (!gameEntity.getGameModes().equals(gameModesFromDto)) {
                gameEntity.setGameModes(gameModesFromDto);
            }
        } else {
            if (!gameEntity.getGameModes().isEmpty()) {
                gameEntity.getGameModes().clear();
            }
        }

        // --- Sincronización de Genres ---
        if (gameDto.getGenres() != null) {
            Set<Genre> genresFromDto = new HashSet<>();
            for (GenreDto dto : gameDto.getGenres()) {
                Genre entity = this.getOrCreateGenre(dto);
                if (entity != null) {
                    genresFromDto.add(entity);
                }
            }
            if (!gameEntity.getGenres().equals(genresFromDto)) {
                gameEntity.setGenres(genresFromDto);
            }
        } else {
            if (!gameEntity.getGenres().isEmpty()) {
                gameEntity.getGenres().clear();
            }
        }

        // --- Sincronización de Franchises ---
        if (gameDto.getFranchises() != null) {
            Set<Franchise> franchisesFromDto = new HashSet<>();
            for (FranchiseDto dto : gameDto.getFranchises()) {
                Franchise entity = this.getOrCreateFranchise(dto);
                if (entity != null) {
                    franchisesFromDto.add(entity);
                }
            }
            if (!gameEntity.getFranchises().equals(franchisesFromDto)) {
                gameEntity.setFranchises(franchisesFromDto);
            }
        } else {
            if (!gameEntity.getFranchises().isEmpty()) {
                gameEntity.getFranchises().clear();
            }
        }

        // --- Sincronización de GameEngines ---
        if (gameDto.getGameEngines() != null) {
            Set<GameEngine> gameEnginesFromDto = new HashSet<>();
            for (GameEngineDto dto : gameDto.getGameEngines()) {
                GameEngine entity = this.getOrCreateGameEngine(dto);
                if (entity != null) {
                    gameEnginesFromDto.add(entity);
                }
            }
            if (!gameEntity.getGameEngines().equals(gameEnginesFromDto)) {
                gameEntity.setGameEngines(gameEnginesFromDto);
            }
        } else {
            if (!gameEntity.getGameEngines().isEmpty()) {
                gameEntity.getGameEngines().clear();
            }
        }

        // --- Sincronización de Keywords ---
        if (gameDto.getKeywords() != null) {
            Set<Keyword> keywordsFromDto = new HashSet<>();
            for (KeywordDto dto : gameDto.getKeywords()) {
                Keyword entity = this.getOrCreateKeyword(dto);
                if (entity != null) {
                    keywordsFromDto.add(entity);
                }
            }
            if (!gameEntity.getKeywords().equals(keywordsFromDto)) {
                gameEntity.setKeywords(keywordsFromDto);
            }
        } else {
            if (!gameEntity.getKeywords().isEmpty()) {
                gameEntity.getKeywords().clear();
            }
        }

        // --- Sincronización de Platforms ---
        if (gameDto.getPlatforms() != null) {
            Set<Platform> platformsFromDto = new HashSet<>();
            for (PlatformDto dto : gameDto.getPlatforms()) {
                Platform entity = this.getOrCreatePlatform(dto);
                if (entity != null) {
                    platformsFromDto.add(entity);
                }
            }
            if (!gameEntity.getPlatforms().equals(platformsFromDto)) {
                gameEntity.setPlatforms(platformsFromDto);
            }
        } else {
            if (!gameEntity.getPlatforms().isEmpty()) {
                gameEntity.getPlatforms().clear();
            }
        }

        // --- Sincronización de Themes ---
        if (gameDto.getThemes() != null) {
            Set<Theme> themesFromDto = new HashSet<>();
            for (ThemeDto dto : gameDto.getThemes()) {
                Theme entity = this.getOrCreateTheme(dto);
                if (entity != null) {
                    themesFromDto.add(entity);
                }
            }
            if (!gameEntity.getThemes().equals(themesFromDto)) {
                gameEntity.setThemes(themesFromDto);
            }
        } else {
            if (!gameEntity.getThemes().isEmpty()) {
                gameEntity.getThemes().clear();
            }
        }
    }

    /**
     * Obtiene o crea una entidad {@link Theme} persistida.
     * Si la entidad ya existe (por IGDB ID), la devuelve. Si no, la crea, la guarda y la devuelve.
     * Actualiza el nombre si es diferente en el DTO.
     *
     * @param dto El {@link ThemeDto} con la información del tema.
     * @return La entidad {@link Theme} persistida, o {@code null} si el DTO es inválido.
     */
    public Theme getOrCreateTheme(ThemeDto dto) {
        // 1. Guarda de validación inicial (salida temprana).
        if (dto == null || dto.getIgdbId() == null) {
            return null;
        }

        Theme themeToReturn;

        Optional<Theme> existingThemeOptional = themeRepository.findByIgdbId(dto.getIgdbId());

        if (existingThemeOptional.isPresent()) {
            Theme existingTheme = existingThemeOptional.get();

            if (!Objects.equals(dto.getName(), existingTheme.getName())) {
                existingTheme.setName(dto.getName());
                themeToReturn = themeRepository.save(existingTheme);
            } else {
                // Si no hay que actualizar, se asigna la entidad existente.
                themeToReturn = existingTheme;
            }
        } else {
            //El tema no existe y debe ser creado.
            Theme newTheme = themeMapper.toEntity(dto);
            themeToReturn = themeRepository.save(newTheme);
        }

        return themeToReturn;
    }

    /**
     * Obtiene o crea una entidad {@link Company} persistida.
     * Si la entidad ya existe (por IGDB ID), la devuelve, actualizando su nombre si es necesario.
     * Si no existe, la crea, la guarda y la devuelve.
     *
     * @param dto El {@link CompanyInfoDto} con la información de la compañía.
     * @return La entidad {@link Company} persistida, o {@code null} si el DTO es inválido.
     */
    private Company getOrCreateCompany(CompanyInfoDto dto) {
        if (dto == null || dto.getIgdbId() == null) {
            logger.warn("CompanyInfoDto nulo o sin IgdbId. No se puede procesar la compañía.");
            return null;
        }
        Optional<Company> existingCompanyOpt = companyRepository.findByIgdbId(dto.getIgdbId());
        Company companyEntity;
        if (existingCompanyOpt.isPresent()) {
            companyEntity = existingCompanyOpt.get();
            if (!Objects.equals(companyEntity.getName(), dto.getName())) {
                logger.debug("Actualizando nombre de Company existente IGDB ID: {} de '{}' a '{}'",
                        dto.getIgdbId(), companyEntity.getName(), dto.getName());
                companyEntity.setName(dto.getName());
                return companyRepository.save(companyEntity);
            }
        } else {
            logger.debug("Creando nueva Company para IGDB ID: {}", dto.getIgdbId());
            companyEntity = companyMapper.toEntity(dto);
            return companyRepository.save(companyEntity);
        }
        return companyEntity;
    }

    /**
     * Obtiene o crea una entidad {@link GameCompanyInvolvement} persistida.
     * Esta entidad representa la relación entre un juego y una compañía, incluyendo su rol.
     * Si la relación ya existe (por {@code involvementIgdbId}), la actualiza si es necesario.
     * Si no existe, la crea, la guarda y la devuelve.
     *
     * @param invDto El {@link InvolvedCompanyDto} con la información de la relación.
     * @param gameEntity La entidad {@link Game} a la que se asocia la compañía.
     * @param companyEntity La entidad {@link Company} involucrada.
     * @return La entidad {@link GameCompanyInvolvement} persistida, o {@code null} si los datos de entrada son insuficientes.
     */
    private GameCompanyInvolvement getOrCreateGameCompanyInvolvement(InvolvedCompanyDto invDto, Game gameEntity, Company companyEntity) {
        if (invDto == null || invDto.getInvolvementIgdbId() == null || companyEntity == null || gameEntity == null) {
            logger.warn("Datos insuficientes para crear o actualizar GameCompanyInvolvement. invDto: {}, gameEntity: {}, companyEntity: {}",
                    invDto, gameEntity != null ? gameEntity.getIgdbId() : "null", companyEntity != null ? companyEntity.getIgdbId() : "null");
            return null;
        }
        Optional<GameCompanyInvolvement> existingInvolvementOpt = gameCompanyInvolvementRepository.findByInvolvementIgdbId(invDto.getInvolvementIgdbId());
        GameCompanyInvolvement involvementEntity;

        if (existingInvolvementOpt.isPresent()) {
            involvementEntity = existingInvolvementOpt.get();
            logger.debug("Actualizando GameCompanyInvolvement existente IGDB ID: {}", invDto.getInvolvementIgdbId());
            boolean changed = false;
            if (!involvementEntity.getGame().getIgdbId().equals(gameEntity.getIgdbId())) {
                logger.warn("GameCompanyInvolvement IGDB ID {} estaba asociado al juego {} pero se está procesando para el juego {}. Re-asociando.",
                        invDto.getInvolvementIgdbId(), involvementEntity.getGame().getIgdbId(), gameEntity.getIgdbId());
                involvementEntity.setGame(gameEntity);
                changed = true;
            }
            if (!involvementEntity.getCompany().getIgdbId().equals(companyEntity.getIgdbId())) {
                involvementEntity.setCompany(companyEntity);
                changed = true;
            }
            if (involvementEntity.isDeveloper() != invDto.isDeveloper()) {
                involvementEntity.setDeveloper(invDto.isDeveloper());
                changed = true;
            }
            if (involvementEntity.isPublisher() != invDto.isPublisher()) {
                involvementEntity.setPublisher(invDto.isPublisher());
                changed = true;
            }
            if (involvementEntity.isPorting() != invDto.isPorting()) {
                involvementEntity.setPorting(invDto.isPorting());
                changed = true;
            }
            if (involvementEntity.isSupporting() != invDto.isSupporting()) {
                involvementEntity.setSupporting(invDto.isSupporting());
                changed = true;
            }
            if (changed) {
                return gameCompanyInvolvementRepository.save(involvementEntity);
            }
        } else {
            logger.debug("Creando nuevo GameCompanyInvolvement para IGDB ID de involucramiento: {}", invDto.getInvolvementIgdbId());
            GameCompanyInvolvement newInvolvement = new GameCompanyInvolvement();
            newInvolvement.setInvolvementIgdbId(invDto.getInvolvementIgdbId());
            newInvolvement.setGame(gameEntity);
            newInvolvement.setCompany(companyEntity);
            newInvolvement.setDeveloper(invDto.isDeveloper());
            newInvolvement.setPublisher(invDto.isPublisher());
            newInvolvement.setPorting(invDto.isPorting());
            newInvolvement.setSupporting(invDto.isSupporting());
            return gameCompanyInvolvementRepository.save(newInvolvement);
        }
        return involvementEntity;
    }

    /**
     * Procesa la colección de compañías involucradas ({@code involved_companies}) de un {@link GameDto}.
     * Para cada compañía en el DTO, obtiene o crea la entidad {@link Company} y luego
     * obtiene o crea la entidad de relación {@link GameCompanyInvolvement}, asociándola al juego principal.
     * Sincroniza la colección {@code involvedCompanies} de la entidad {@link Game} con el conjunto procesado.
     * Si el DTO no contiene información de compañías o está vacía, se limpian las asociaciones existentes en la entidad.
     *
     * @param gameDto El DTO del juego.
     * @param gameEntity La entidad {@link Game} a actualizar.
     */
    private void processInvolvedCompanies(GameDto gameDto, Game gameEntity) {
        logger.debug("Procesando InvolvedCompanies para el juego {} (IGDB ID: {})", gameEntity.getName(), gameEntity.getIgdbId());
        if (gameDto.getInvolvedCompanies() == null || gameDto.getInvolvedCompanies().isEmpty()) {
            if (!gameEntity.getInvolvedCompanies().isEmpty()) {
                logger.debug("DTO no contiene 'involved_companies' o está vacía. Limpiando {} asociaciones existentes para el juego {}.",
                        gameEntity.getInvolvedCompanies().size(), gameEntity.getIgdbId());
                gameEntity.getInvolvedCompanies().clear();
            }
            return;
        }
        Set<GameCompanyInvolvement> processedInvolvements = new HashSet<>();
        List<InvolvedCompanyDto> involvedCompanyDtos = gameDto.getInvolvedCompanies();
        for (InvolvedCompanyDto invDto : involvedCompanyDtos) {
            if (invDto == null || invDto.getInvolvementIgdbId() == null) {
                logger.warn("Se encontró un InvolvedCompanyDto nulo o sin ID de involucramiento. Saltando.");
                continue;
            }
            CompanyInfoDto companyInfoDto = invDto.getCompany();
            if (companyInfoDto == null || companyInfoDto.getIgdbId() == null) {
                logger.warn("InvolvedCompany (ID de involucramiento: {}) tiene CompanyInfo nula o CompanyInfo.igdbId nulo. Saltando.",
                        invDto.getInvolvementIgdbId());
                continue;
            }
            Company companyEntity = getOrCreateCompany(companyInfoDto);
            if (companyEntity == null) {
                logger.warn("No se pudo obtener o crear la entidad Company para CompanyInfoDto con IGDB ID: {}. Saltando este InvolvedCompany.",
                        companyInfoDto.getIgdbId());
                continue;
            }
            GameCompanyInvolvement involvementEntity = getOrCreateGameCompanyInvolvement(invDto, gameEntity, companyEntity);
            if (involvementEntity != null) {
                processedInvolvements.add(involvementEntity);
            }
        }
        if (!gameEntity.getInvolvedCompanies().equals(processedInvolvements)) {
            logger.debug("Actualizando la colección 'involvedCompanies' del juego {}. Antes: {}, Después: {}.",
                    gameEntity.getIgdbId(), gameEntity.getInvolvedCompanies().size(), processedInvolvements.size());
            gameEntity.getInvolvedCompanies().clear();
            gameEntity.getInvolvedCompanies().addAll(processedInvolvements);
        } else {
            logger.debug("La colección 'involvedCompanies' del juego {} no ha cambiado. No se requiere actualización de la colección en sí.", gameEntity.getIgdbId());
        }
    }

    /**
     * {@inheritDoc}
     * Este método es para obtener la entidad {@link Game} directamente.
     * Incluye la inicialización de todas las colecciones cargadas de forma perezosa (LAZY)
     * usando {@code Hibernate.initialize()} para asegurar que la entidad devuelta esté
     * completamente poblada y lista para ser utilizada, por ejemplo, al mapear a un DTO detallado.
     */
    public Game getGameByIgdbIdOriginal(Long igdbId) {
        // 1. Búsqueda inicial del dato.
        Optional<Game> gameOptional = gameRepository.findByIgdbId(igdbId);

        if (gameOptional.isPresent()) {
            // Flujo de Éxito: El juego fue encontrado.
            Game game = gameOptional.get();

            // Se delega la tarea repetitiva a un método privado.
            initializeLazyCollections(game);

            // Se retorna el resultado final.
            return game;
        } else {
            // Flujo de Falla: El juego no fue encontrado.
            return null;
        }
    }
}