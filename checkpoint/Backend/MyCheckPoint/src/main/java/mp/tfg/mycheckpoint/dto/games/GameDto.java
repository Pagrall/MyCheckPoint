package mp.tfg.mycheckpoint.dto.games;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import mp.tfg.mycheckpoint.dto.enums.GameType;
import mp.tfg.mycheckpoint.dto.enums.ReleaseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO completo que representa un videojuego con todos sus detalles.
 * Esta clase agrega información de diversas fuentes y relaciones.
 */
@Schema(description = "DTO completo para un videojuego, incluyendo todos sus detalles y relaciones.")
@Data
public class GameDto {
    /**
     * ID único del juego en la base de datos de IGDB.
     */
    @Schema(description = "ID único del juego en IGDB.", example = "1020")
    @JsonProperty("id")
    private Long igdbId;

    /**
     * Información de la carátula principal del juego.
     */
    @Schema(description = "Información de la carátula del juego.")
    private CoverDto cover;

    /**
     * Lista de modos de juego disponibles (ej. Un jugador, Multijugador). Puede estar vacía.
     */
    @Schema(description = "Modos de juego disponibles (ej. Un jugador, Multijugador).", nullable = true)
    @JsonProperty("game_modes")
    private List<GameModeDto> gameModes = new ArrayList<>();

    /**
     * Lista de géneros a los que pertenece el juego (ej. RPG, Acción). Puede estar vacía.
     */
    @Schema(description = "Géneros a los que pertenece el juego (ej. RPG, Acción).", nullable = true)
    private List<GenreDto> genres = new ArrayList<>();

    /**
     * Lista de obras de arte (artworks) asociadas al juego. Puede estar vacía.
     */
    @Schema(description = "Información de los artes del juego.", nullable = true)
    @JsonProperty("artworks")
    private List<ArtworkDto> artworks = new ArrayList<>();

    /**
     * Nombre completo del juego.
     */
    @Schema(description = "Nombre completo del juego.", example = "The Legend of Zelda: Breath of the Wild")
    private String name;

    /**
     * Fecha del primer lanzamiento del juego, representada como un timestamp Unix en segundos.
     * Puede ser nula si no está definida.
     */
    @Schema(description = "Fecha del primer lanzamiento del juego (timestamp Unix en segundos).", example = "1488499200", format = "int64", nullable = true)
    @JsonProperty("first_release_date")
    private Long firstReleaseDate;

    /**
     * Estado de lanzamiento del juego (ej. Released, Alpha, Beta).
     * Derivado de {@link GameStatusDto}.
     */
    @Schema(description = "Estado de lanzamiento del juego.", nullable = true)
    @JsonProperty("first_release_status")
    private ReleaseStatus firstReleaseStatus;

    /**
     * Lista de franquicias a las que pertenece el juego. Puede estar vacía.
     */
    @Schema(description = "Franquicias a las que pertenece el juego.", nullable = true)
    @JsonProperty("franchises")
    private List<FranchiseDto> franchises = new ArrayList<>();

    /**
     * Lista de motores de juego utilizados en el desarrollo del juego. Puede estar vacía.
     */
    @Schema(description = "Motores de juego utilizados.", nullable = true)
    @JsonProperty("game_engines")
    private List<GameEngineDto> gameEngines = new ArrayList<>();

    /**
     * Lista de palabras clave asociadas al juego. Puede estar vacía.
     */
    @Schema(description = "Palabras clave asociadas al juego.", nullable = true)
    @JsonProperty("keywords")
    private List<KeywordDto> keywords = new ArrayList<>();

    /**
     * Lista de plataformas en las que el juego está disponible. Puede estar vacía.
     */
    @Schema(description = "Plataformas en las que el juego está disponible.", nullable = true)
    @JsonProperty("platforms")
    private List<PlatformDto> platforms = new ArrayList<>();

    /**
     * Lista de capturas de pantalla del juego. Puede estar vacía.
     */
    @Schema(description = "Capturas de pantalla del juego.", nullable = true)
    @JsonProperty("screenshots")
    private List<ScreenshotDto> screenshots = new ArrayList<>();

    /**
     * Lista de sitios web relacionados con el juego. Puede estar vacía.
     */
    @Schema(description = "Sitios web relacionados con el juego.", nullable = true)
    @JsonProperty("websites")
    private List<WebsiteDto> websites = new ArrayList<>();

    /**
     * Lista de vídeos relacionados con el juego. Puede estar vacía.
     */
    @Schema(description = "Vídeos relacionados con el juego.", nullable = true)
    @JsonProperty("videos")
    private List<VideoDto> videos = new ArrayList<>();

    /**
     * Calificación total del juego, usualmente un promedio de críticas o de usuarios.
     * Puede ser nulo.
     */
    @Schema(description = "Calificación total del juego (promedio de usuarios/críticos).", example = "97.0", nullable = true)
    @JsonProperty("total_rating")
    private Double totalRating;

    /**
     * Número total de calificaciones recibidas que contribuyen a {@code totalRating}.
     * Puede ser nulo.
     */
    @Schema(description = "Número total de calificaciones recibidas.", example = "2500", nullable = true)
    @JsonProperty("total_rating_count")
    private Integer totalRatingCount;

    /**
     * Lista de temas principales del juego (ej. Fantasía, Ciencia Ficción). Puede estar vacía.
     */
    @Schema(description = "Temas principales del juego (ej. Fantasía, Ciencia Ficción).", nullable = true)
    @JsonProperty("themes")
    private List<ThemeDto> themes = new ArrayList<>();

    /**
     * Identificador URL amigable del juego (slug).
     * Puede ser nulo.
     */
    @Schema(description = "Identificador URL amigable del juego.", example = "the-legend-of-zelda-breath-of-the-wild", nullable = true)
    private String slug;

    /**
     * Resumen o sinopsis breve del juego.
     * Puede ser nulo.
     */
    @Schema(description = "Resumen o sinopsis del juego.", example = "Una aventura épica en un vasto mundo abierto...", nullable = true)
    private String summary;

    /**
     * Descripción de la historia o argumento principal del juego.
     * Puede ser nulo.
     */
    @Schema(description = "Argumento o historia principal del juego.", example = "Link despierta después de 100 años...", nullable = true)
    private String storyline;

    /**
     * Tipo de juego (ej. Juego Principal, DLC, Expansión).
     */
    @Schema(description = "Tipo de juego (ej. JUEGO_PRINCIPAL, DLC, EXPANSION).", example = "GAME")
    @JsonProperty("game_type")
    private GameType gameType;

    /**
     * Información del juego padre, si este DTO representa una expansión, DLC, etc.
     * Será nulo si este es un juego base.
     */
    @Schema(description = "Información del juego padre, si este es una expansión o DLC.", nullable = true)
    @JsonProperty("parent_game")
    private DlcInfoDto parentGameInfo;

    /**
     * Lista de DLCs (Contenido Descargable) para este juego. Puede estar vacía.
     */
    @Schema(description = "Lista de DLCs para este juego.", nullable = true)
    @JsonProperty("dlcs")
    private List<DlcInfoDto> dlcs = new ArrayList<>();

    /**
     * Lista de expansiones para este juego. Puede estar vacía.
     */
    @Schema(description = "Lista de expansiones para este juego.", nullable = true)
    @JsonProperty("expansions")
    private List<DlcInfoDto> expansions = new ArrayList<>();

    /**
     * Lista de paquetes (bundles) que incluyen este juego o de los que este juego forma parte.
     * Puede estar vacía.
     */
    @Schema(description = "Lista de paquetes/bundles que incluyen este juego o de los que este juego forma parte.", nullable = true)
    @JsonProperty("bundles")
    private List<DlcInfoDto> bundles = new ArrayList<>();

    /**
     * Información del juego base si este DTO representa una versión específica (ej. Edición GOTY).
     * Será nulo si no es una versión de otro juego.
     */
    @Schema(description = "Información del juego base si este es una versión específica (ej. Edición GOTY).", nullable = true)
    @JsonProperty("version_parent")
    private DlcInfoDto versionParent;

    /**
     * Lista de remakes de este juego. Puede estar vacía.
     */
    @Schema(description = "Lista de remakes de este juego.", nullable = true)
    @JsonProperty("remakes")
    private List<DlcInfoDto> remakes = new ArrayList<>();

    /**
     * Lista de remasters de este juego. Puede estar vacía.
     */
    @Schema(description = "Lista de remasters de este juego.", nullable = true)
    @JsonProperty("remasters")
    private List<DlcInfoDto> remasters = new ArrayList<>();

    /**
     * Lista de juegos similares a este. Puede estar vacía.
     */
    @Schema(description = "Lista de juegos similares a este.", nullable = true)
    @JsonProperty("similar_games")
    private List<SimilarGameInfoDto> similarGames = new ArrayList<>();

    /**
     * Lista de compañías involucradas en el desarrollo y/o publicación del juego.
     * Puede estar vacía.
     */
    @Schema(description = "Compañías involucradas en el desarrollo/publicación del juego.", nullable = true)
    @JsonProperty("involved_companies")
    private List<InvolvedCompanyDto> involvedCompanies = new ArrayList<>();

    /**
     * Estado numérico del juego según IGDB, utilizado para determinar {@link ReleaseStatus}.
     * Puede ser nulo.
     */
    @Schema(description = "Estado numérico del juego según IGDB (mapeado a ReleaseStatus).", nullable = true)
    @JsonProperty("game_status")
    private GameStatusDto gameStatus;

    /**
     * Indicador interno utilizado por el servicio para determinar si este DTO
     * contiene todos los detalles de un juego o solo un subconjunto (ej. de una búsqueda).
     * Por defecto es {@code true}. Este campo se ignora en la serialización/deserialización JSON
     * y no se muestra en la documentación de la API.
     */
    @JsonIgnore
    @Schema(hidden = true)
    private boolean isFullDetails = true;
}