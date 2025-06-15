package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.DlcInfoDto;
import mp.tfg.mycheckpoint.dto.games.GameDto;
import mp.tfg.mycheckpoint.dto.games.SimilarGameInfoDto;
import mp.tfg.mycheckpoint.entity.games.Game;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper principal para la conversión entre la entidad {@link Game} y su DTO {@link GameDto}.
 * Este mapper es complejo y utiliza múltiples otros mappers para las diversas
 * colecciones y objetos anidados que componen la información de un juego.
 * También incluye lógica personalizada ({@code @AfterMapping}) para categorizar juegos hijos.
 */
@Mapper(componentModel = "spring", uses = {
        CoverMapper.class, GameModeMapper.class, GenreMapper.class, ArtworkMapper.class,
        FranchiseMapper.class, GameEngineMapper.class, KeywordMapper.class, PlatformMapper.class,
        ScreenshotMapper.class, WebsiteMapper.class, VideoMapper.class, ThemeMapper.class,
        InvolvedCompanyMapper.class
})
public interface GameMapper {

    /**
     * Convierte una entidad {@link Game} (usualmente un juego hijo o relacionado)
     * a un {@link DlcInfoDto} que contiene información resumida.
     *
     * @param gameEntity La entidad Game a convertir.
     * @return El DlcInfoDto resultante.
     */
    @Mapping(source = "igdbId", target = "igdbId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "cover", target = "cover")
    @Mapping(source = "totalRating", target = "totalRating")
    @Mapping(source = "gameType", target = "gameType")
    @Mapping(source = "slug", target = "slug")
    DlcInfoDto gameEntityToDlcInfoDto(Game gameEntity);

    /**
     * Convierte una entidad {@link Game} (usualmente un juego similar)
     * a un {@link SimilarGameInfoDto} que contiene información resumida.
     *
     * @param gameEntity La entidad Game a convertir.
     * @return El SimilarGameInfoDto resultante.
     */
    @Mapping(source = "igdbId", target = "igdbId")
    @Mapping(source = "cover", target = "cover")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "slug", target = "slug")
    @Mapping(source = "summary", target = "summary")
    @Mapping(source = "totalRating", target = "totalRating")
    SimilarGameInfoDto gameEntityToSimilarGameInfoDto(Game gameEntity);

    /**
     * Convierte una entidad {@link Game} completa a su DTO {@link GameDto} detallado.
     * Mapea todos los campos directos y colecciones utilizando los mappers especificados en {@code uses}.
     * La lógica para categorizar juegos hijos (DLCs, expansiones, bundles) se aplica después del mapeo inicial.
     *
     * @param game La entidad Game a convertir.
     * @return El GameDto resultante con todos los detalles.
     */
    @Mapping(source = "parentGame", target = "parentGameInfo")
    @Mapping(source = "versionParentGame", target = "versionParent")
    @Mapping(source = "remakeVersions", target = "remakes")
    @Mapping(source = "remasterVersions", target = "remasters")
    @Mapping(source = "similarGames", target = "similarGames")
    @Mapping(source = "involvedCompanies", target = "involvedCompanies")
    @Mapping(source = "firstReleaseStatus", target = "firstReleaseStatus")
    GameDto toDto(Game game);

    /**
     * Método ejecutado después del mapeo de {@link Game} a {@link GameDto}.
     * Se encarga de poblar las listas {@code dlcs}, {@code expansions}, y {@code bundles} en el {@link GameDto}
     * categorizando los juegos presentes en la colección {@code childGames} de la entidad {@link Game}.
     *
     * @param game La entidad Game fuente.
     * @param gameDto El GameDto destino que se está poblando.
     */
    @AfterMapping
    default void mapChildGamesToCategorizedLists(Game game, @MappingTarget GameDto gameDto) {
        List<DlcInfoDto> dlcList = new ArrayList<>();
        List<DlcInfoDto> expansionList = new ArrayList<>();
        List<DlcInfoDto> bundleList = new ArrayList<>();

        if (game.getChildGames() != null && !game.getChildGames().isEmpty()) {
            for (Game childGame : game.getChildGames()) {
                DlcInfoDto infoDto = gameEntityToDlcInfoDto(childGame); // Usa el metodo de esta misma interfaz
                if (infoDto != null && childGame.getGameType() != null) {
                    switch (childGame.getGameType()) {
                        case DLC:
                            dlcList.add(infoDto);
                            break;
                        case EXPANSION:
                        case STANDALONE_EXPANSION: // Agrupamos expansiones standalone también
                            expansionList.add(infoDto);
                            break;
                        case BUNDLE:
                            bundleList.add(infoDto);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        gameDto.setDlcs(dlcList);
        gameDto.setExpansions(expansionList);
        gameDto.setBundles(bundleList);
    }

    /**
     * Convierte un {@link GameDto} a una entidad {@link Game} para persistencia o actualización.
     * Ignora el ID interno y varias colecciones/relaciones que se gestionan de forma separada
     * o más compleja en la capa de servicio (especialmente las relaciones ManyToMany y OneToMany inversas).
     *
     * @param gameDto El GameDto a convertir.
     * @return La entidad Game resultante, parcialmente poblada.
     */
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "parentGame", ignore = true) // Se manejan en el servicio
    @Mapping(target = "childGames", ignore = true) // Se manejan en el servicio
    @Mapping(target = "versionParentGame", ignore = true) // Se manejan en el servicio
    @Mapping(target = "remakeVersions", ignore = true) // Se manejan en el servicio
    @Mapping(target = "remasterVersions", ignore = true) // Se manejan en el servicio
    @Mapping(target = "similarGames", ignore = true) // Se manejan en el servicio
    @Mapping(target = "involvedCompanies", ignore = true) // Se manejan en el servicio
    @Mapping(target = "gameModes", ignore = true) // Se manejan en el servicio (getOrCreate)
    @Mapping(target = "genres", ignore = true) // Se manejan en el servicio (getOrCreate)
    @Mapping(target = "themes", ignore = true) // Se manejan en el servicio (getOrCreate)
    @Mapping(target = "keywords", ignore = true) // Se manejan en el servicio (getOrCreate)
    @Mapping(target = "platforms", ignore = true) // Se manejan en el servicio (getOrCreate)
    @Mapping(target = "gameEngines", ignore = true) // Se manejan en el servicio (getOrCreate)
    @Mapping(target = "franchises", ignore = true) // Se manejan en el servicio (getOrCreate)
    @Mapping(target = "firstReleaseStatus", ignore = true) // Se deriva de gameStatus.id en el servicio
    // El campo 'gameStatus' (GameStatusDto) del DTO se usa para poblar 'firstReleaseStatus' en la entidad.
    // El campo 'firstReleaseDate' se mapea con mapTimestampToInstant.
    Game toEntity(GameDto gameDto);

    /**
     * Actualiza una entidad {@link Game} existente con los datos de un {@link GameDto}.
     * Similar a {@code toEntity}, ignora campos que se gestionan por separado.
     * Este método es útil para actualizaciones parciales donde solo los campos básicos y
     * algunas colecciones embebidas directas del DTO deben transferirse.
     * Las relaciones complejas y ManyToMany se suelen manejar en el servicio.
     *
     * @param gameDto El DTO con los datos de actualización.
     * @param game La entidad Game a actualizar (anotada con {@link MappingTarget}).
     */
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "parentGame", ignore = true)
    @Mapping(target = "childGames", ignore = true)
    @Mapping(target = "versionParentGame", ignore = true)
    @Mapping(target = "remakeVersions", ignore = true)
    @Mapping(target = "remasterVersions", ignore = true)
    @Mapping(target = "similarGames", ignore = true)
    @Mapping(target = "involvedCompanies", ignore = true)
    @Mapping(target = "gameModes", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "themes", ignore = true)
    @Mapping(target = "keywords", ignore = true)
    @Mapping(target = "platforms", ignore = true)
    @Mapping(target = "gameEngines", ignore = true)
    @Mapping(target = "franchises", ignore = true)
    @Mapping(target = "firstReleaseStatus", ignore = true)
    void updateFromDto(GameDto gameDto, @MappingTarget Game game);

    /**
     * Convierte un timestamp Unix (segundos desde la época) a un objeto {@link Instant}.
     * @param timestamp El timestamp en segundos, o {@code null}.
     * @return El {@link Instant} correspondiente, o {@code null} si el input es {@code null}.
     */
    default Instant mapTimestampToInstant(Long timestamp) {
        if (timestamp == null) return null;
        return Instant.ofEpochSecond(timestamp);
    }

    /**
     * Convierte un objeto {@link Instant} a un timestamp Unix (segundos desde la época).
     * @param instant El {@link Instant}, o {@code null}.
     * @return El timestamp en segundos, o {@code null} si el input es {@code null}.
     */
    default Long mapInstantToTimestamp(Instant instant) {
        if (instant == null) return null;
        return instant.getEpochSecond();
    }
}