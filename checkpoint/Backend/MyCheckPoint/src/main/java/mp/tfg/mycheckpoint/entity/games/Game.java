package mp.tfg.mycheckpoint.entity.games;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import mp.tfg.mycheckpoint.dto.enums.GameType;
import mp.tfg.mycheckpoint.dto.enums.ReleaseStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

/**
 * Entidad que representa un videojuego.
 * Agrega información detallada del juego, incluyendo sus relaciones con otros
 * juegos (DLCs, expansiones, remakes, etc.), plataformas, géneros, compañías y más.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "games")
public class Game {

    /**
     * Identificador interno único del juego en la base de datos local (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    /**
     * ID único del juego proveniente de una fuente externa (ej. IGDB).
     * Este ID debe ser único y no nulo.
     */
    @Column(unique = true, nullable = false)
    private Long igdbId;

    /**
     * Nombre del juego.
     */
    private String name;

    /**
     * Identificador URL amigable del juego (slug).
     */
    private String slug;

    /**
     * Información de la carátula principal del juego.
     * Se almacena como un objeto embebido {@link Cover}.
     */
    @Embedded
    private Cover cover;

    /**
     * Calificación total del juego, usualmente un promedio. Puede ser nulo.
     */
    @Column(name = "total_rating")
    private Double totalRating;

    /**
     * Número total de calificaciones recibidas que contribuyen a {@code totalRating}. Puede ser nulo.
     */
    @Column(name = "total_rating_count")
    private Integer totalRatingCount;

    /**
     * Resumen o sinopsis breve del juego. Almacenado como TEXT.
     */
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    /**
     * Descripción de la historia o argumento principal del juego. Almacenado como TEXT.
     */
    @Column(name = "storyline", columnDefinition = "TEXT")
    private String storyline;

    /**
     * Indica si la información almacenada para este juego en la base de datos local es completa
     * (obtenida de una sincronización detallada) o parcial (ej. de una búsqueda).
     * Por defecto es {@code false}. No puede ser nulo.
     */
    @Column(name = "is_full_details", nullable = false)
    private boolean isFullDetails = false;

    /**
     * Tipo de juego (ej. Juego Principal, DLC, Expansión).
     * Se almacena como el valor ordinal del enum {@link GameType}.
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "game_type")
    private GameType gameType;

    /**
     * Fecha del primer lanzamiento del juego, representada como un {@link Instant}.
     * Puede ser nulo si no está definida.
     */
    @Column(name = "first_release_date")
    private Instant firstReleaseDate;

    /**
     * Estado de lanzamiento del juego (ej. Released, Alpha, Beta).
     * Se almacena como una cadena representando el nombre del enum {@link ReleaseStatus}.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "first_release_status")
    private ReleaseStatus firstReleaseStatus;

    /**
     * Juego padre, si este juego es una expansión, DLC, o una versión.
     * La carga es perezosa (LAZY). Las operaciones de persistencia y fusión se propagan.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "parent_game_internal_id")
    private Game parentGame;

    /**
     * Conjunto de juegos hijos (DLCs, expansiones, etc.) de este juego.
     * La carga es perezosa (LAZY).
     */
    @OneToMany(mappedBy = "parentGame", fetch = FetchType.LAZY)
    private Set<Game> childGames = new HashSet<>();

    /**
     * Juego base si este juego es una versión específica (ej. Edición GOTY).
     * La carga es perezosa (LAZY). Las operaciones de persistencia y fusión se propagan.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "version_parent_game_internal_id")
    private Game versionParentGame;

    /**
     * Conjunto de remakes asociados a este juego (si este es el juego original).
     * La relación es muchos a muchos, gestionada a través de una tabla de unión.
     * Las operaciones de persistencia y fusión se propagan.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_remake_versions_assoc",
            joinColumns = @JoinColumn(name = "original_game_id"),
            inverseJoinColumns = @JoinColumn(name = "remake_game_id"))
    private Set<Game> remakeVersions = new HashSet<>();

    /**
     * Conjunto de remasters asociados a este juego (si este es el juego original).
     * La relación es muchos a muchos, gestionada a través de una tabla de unión.
     * Las operaciones de persistencia y fusión se propagan.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_remaster_versions_assoc",
            joinColumns = @JoinColumn(name = "original_game_id"),
            inverseJoinColumns = @JoinColumn(name = "remaster_game_id"))
    private Set<Game> remasterVersions = new HashSet<>();

    /**
     * Lista de obras de arte (artworks) asociadas al juego.
     * Se almacenan como una colección de elementos embebidos {@link Artwork}.
     * La carga es perezosa (LAZY).
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "game_artworks", joinColumns = @JoinColumn(name = "game_internal_id"))
    private List<Artwork> artworks = new ArrayList<>();

    /**
     * Lista de capturas de pantalla del juego.
     * Se almacenan como una colección de elementos embebidos {@link Screenshot}.
     * La carga es perezosa (LAZY).
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "game_screenshots", joinColumns = @JoinColumn(name = "game_internal_id"))
    private List<Screenshot> screenshots = new ArrayList<>();

    /**
     * Lista de sitios web relacionados con el juego.
     * Se almacenan como una colección de elementos embebidos {@link Website}.
     * La carga es perezosa (LAZY).
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "game_websites", joinColumns = @JoinColumn(name = "game_internal_id"))
    private List<Website> websites = new ArrayList<>();

    /**
     * Lista de vídeos relacionados con el juego.
     * Se almacenan como una colección de elementos embebidos {@link Video}.
     * La carga es perezosa (LAZY).
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "game_videos", joinColumns = @JoinColumn(name = "game_internal_id"))
    private List<Video> videos = new ArrayList<>();

    /**
     * Conjunto de modos de juego disponibles.
     * Relación muchos a muchos con {@link GameMode}. Persistencia y fusión propagadas.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_game_modes",
            joinColumns = @JoinColumn(name = "game_internal_id"),
            inverseJoinColumns = @JoinColumn(name = "game_mode_internal_id"))
    private Set<GameMode> gameModes = new HashSet<>();

    /**
     * Conjunto de géneros a los que pertenece el juego.
     * Relación muchos a muchos con {@link Genre}. Persistencia y fusión propagadas.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_genres",
            joinColumns = @JoinColumn(name = "game_internal_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_internal_id"))
    private Set<Genre> genres = new HashSet<>();

    /**
     * Conjunto de franquicias a las que pertenece el juego.
     * Relación muchos a muchos con {@link Franchise}. Persistencia y fusión propagadas.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_franchises",
            joinColumns = @JoinColumn(name = "game_internal_id"),
            inverseJoinColumns = @JoinColumn(name = "franchise_internal_id"))
    private Set<Franchise> franchises = new HashSet<>();

    /**
     * Conjunto de motores de juego utilizados.
     * Relación muchos a muchos con {@link GameEngine}. Persistencia y fusión propagadas.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_game_engines_assoc",
            joinColumns = @JoinColumn(name = "game_internal_id"),
            inverseJoinColumns = @JoinColumn(name = "game_engine_internal_id"))
    private Set<GameEngine> gameEngines = new HashSet<>();

    /**
     * Conjunto de palabras clave asociadas al juego.
     * Relación muchos a muchos con {@link Keyword}. Persistencia y fusión propagadas.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_keywords_assoc",
            joinColumns = @JoinColumn(name = "game_internal_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_internal_id"))
    private Set<Keyword> keywords = new HashSet<>();

    /**
     * Conjunto de plataformas en las que el juego está disponible.
     * Relación muchos a muchos con {@link Platform}. Persistencia y fusión propagadas.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_platforms_assoc",
            joinColumns = @JoinColumn(name = "game_internal_id"),
            inverseJoinColumns = @JoinColumn(name = "platform_internal_id"))
    private Set<Platform> platforms = new HashSet<>();

    /**
     * Conjunto de temas principales del juego.
     * Relación muchos a muchos con {@link Theme}. Persistencia y fusión propagadas.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_themes_assoc",
            joinColumns = @JoinColumn(name = "game_internal_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_internal_id"))
    private Set<Theme> themes = new HashSet<>();

    /**
     * Conjunto de juegos similares a este.
     * Relación muchos a muchos (autoreferencia gestionada por JPA) con {@link Game}.
     * Persistencia y fusión propagadas.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_similar_games_assoc",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "similar_game_id"))
    private Set<Game> similarGames = new HashSet<>();

    /**
     * Conjunto de compañías involucradas en el juego y sus roles.
     * Relación uno a muchos con {@link GameCompanyInvolvement}.
     * Todas las operaciones de persistencia se propagan y los huérfanos se eliminan.
     * La carga es perezosa (LAZY).
     */
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<GameCompanyInvolvement> involvedCompanies = new HashSet<>();

    /**
     * Compara este Game con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code igdbId}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(igdbId, game.igdbId);
    }

    /**
     * Genera un código hash para este Game.
     * El hash se basa en el {@code igdbId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(igdbId);
    }
}