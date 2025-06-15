package mp.tfg.mycheckpoint.service.games;


import mp.tfg.mycheckpoint.dto.games.GameDto;
import mp.tfg.mycheckpoint.dto.games.GameModeDto;
import mp.tfg.mycheckpoint.dto.games.GenreDto;
import mp.tfg.mycheckpoint.dto.games.ThemeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para interactuar con la API de IGDB (Internet Game Database).
 * Este servicio utiliza un {@link WebClient} configurado para realizar solicitudes HTTP
 * a la API de IGDB y obtener información sobre videojuegos. Proporciona métodos
 * para buscar juegos por nombre, obtener detalles de un juego por su ID de IGDB,
 * y filtrar juegos por múltiples criterios.
 */
@Service
public class IgdbService {

    private static final Logger logger = LoggerFactory.getLogger(IgdbService.class);
    private final WebClient igdbWebClient;

    /**
     * Cadena de consulta de campos por defecto utilizada para solicitar información detallada
     * de un juego a la API de IGDB. Incluye una amplia gama de atributos del juego
     * y sus relaciones (carátula, artworks, géneros, plataformas, etc.).
     */
    private static final String DEFAULT_GAME_FIELDS = """
            fields
                id, name, slug, summary, storyline, first_release_date, total_rating, total_rating_count, game_type,
                cover.id, cover.url,
                artworks.id, artworks.url,
                screenshots.id, screenshots.url,
                videos.id, videos.name, videos.video_id,
                websites.id, websites.url,
                game_modes.id, game_modes.name,
                genres.id, genres.name,
                themes.id, themes.name,
                keywords.id, keywords.name,
                game_engines.id, game_engines.name,
                franchises.id, franchises.name,
                platforms.id, platforms.name, platforms.alternative_name, platforms.platform_logo.id, platforms.platform_logo.url,
                involved_companies.id, involved_companies.developer, involved_companies.publisher, involved_companies.porting, involved_companies.supporting,
                involved_companies.company.id, involved_companies.company.name,
                dlcs.id, dlcs.name, dlcs.slug, dlcs.game_type, dlcs.total_rating, dlcs.cover.id, dlcs.cover.url,
                expansions.id, expansions.name, expansions.slug, expansions.game_type, expansions.total_rating, expansions.cover.id, expansions.cover.url,
                bundles.id, bundles.name, bundles.slug, bundles.game_type, bundles.total_rating, bundles.cover.id, bundles.cover.url,
                similar_games.id, similar_games.name, similar_games.slug, similar_games.summary, similar_games.total_rating, similar_games.cover.id, similar_games.cover.url,
                parent_game.id, parent_game.name, parent_game.slug, parent_game.game_type, parent_game.total_rating, parent_game.cover.id, parent_game.cover.url,
                version_parent.id, version_parent.name, version_parent.slug, version_parent.game_type, version_parent.total_rating, version_parent.cover.id, version_parent.cover.url,
                remakes.id, remakes.name, remakes.slug, remakes.game_type, remakes.total_rating, remakes.cover.id, remakes.cover.url,
                remasters.id, remasters.name, remasters.slug, remasters.game_type, remasters.total_rating, remasters.cover.id, remasters.cover.url,
                game_status.id;
            """;

    /**
     * Constructor para IgdbService.
     *
     * @param igdbWebClient El {@link WebClient} preconfigurado (inyectado por Spring)
     * para realizar las llamadas a la API de IGDB.
     */
    @Autowired
    public IgdbService(WebClient igdbWebClient) {
        this.igdbWebClient = igdbWebClient;
    }

    /**
     * Busca juegos en la API de IGDB utilizando un término de búsqueda para el nombre.
     * Solicita un conjunto limitado de campos para optimizar la respuesta (nombre, calificación, carátula, etc.).
     * Limita los resultados a un máximo de 15 juegos.
     *
     * @param gameName El nombre o término de búsqueda del juego. Las comillas dobles en el nombre serán escapadas.
     * @return Un {@link Flux} que emite objetos {@link GameDto} con la información resumida de los juegos encontrados.
     * En caso de error en la llamada a la API o deserialización, el Flux emitirá un error.
     */
    public Flux<GameDto> findGamesByName(String gameName) {
        String fields = "fields name, total_rating, cover.url, first_release_date,game_type,summary, id;";
        String queryBody = fields + "search \"" + gameName.replace("\"", "\\\"") + "\"; limit 500;";
        logger.info("Querying IGDB (findGamesByName) with body: {}", queryBody);
        return igdbWebClient.post()
                .uri("/games")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(queryBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    logger.error("Error from IGDB API (findGamesByName): Status {}, Body: {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("IGDB API Error (findGamesByName): " + clientResponse.statusCode() + " - " + errorBody));
                                }))
                .bodyToFlux(GameDto.class)
                .doOnError(error -> logger.error("Error during IGDB call or deserialization (findGamesByName) for query [{}]: {}", gameName, error.getMessage(), error));
    }

    /**
     * Busca un juego específico en la API de IGDB por su ID único de IGDB.
     * Utiliza la constante {@code DEFAULT_GAME_FIELDS} para solicitar un conjunto completo de datos del juego.
     *
     * @param igdbId El ID de IGDB del juego a buscar.
     * @return Un {@link Mono} que emite un objeto {@link GameDto} con la información detallada del juego.
     * Si el juego no se encuentra, el Mono se completará vacío.
     * En caso de error en la llamada a la API o deserialización, el Mono emitirá un error.
     */
    public Mono<GameDto> findGameByIgdbId(Long igdbId) {
        String queryBody = DEFAULT_GAME_FIELDS + "where id = " + igdbId + ";";
        logger.info("Querying IGDB (findGameByIgdbId) for ID {}: {}", igdbId, queryBody);
        return igdbWebClient.post()
                .uri("/games")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(queryBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    logger.error("Error from IGDB API (findGameByIgdbId) for ID {}: Status {}, Body: {}", igdbId, clientResponse.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("IGDB API Error (findGameByIgdbId): " + clientResponse.statusCode() + " - " + errorBody));
                                }))
                .bodyToFlux(GameDto.class) // IGDB devuelve un array, incluso para un solo resultado por ID.
                .next() // Se toma el primer (y único esperado) elemento.
                .doOnError(error -> logger.error("Error during IGDB call or deserialization (findGameByIgdbId) for ID {}: {}", igdbId, error.getMessage(), error));
    }

    /**
     * Busca juegos en la API de IGDB aplicando un conjunto de filtros personalizables,
     * como rango de fechas de lanzamiento, ID de género, ID de tema, ID de modo de juego y un límite de resultados.
     * Solicita un conjunto específico de campos para los juegos resultantes (nombre, calificación, carátula, etc.).
     *
     * @param releaseDateStart Timestamp Unix (en segundos) para la fecha de inicio del rango de lanzamiento. Opcional.
     * @param releaseDateEnd Timestamp Unix (en segundos) para la fecha de fin del rango de lanzamiento. Opcional.
     * @param genreId ID del género según IGDB para filtrar. Opcional.
     * @param themeId ID del tema según IGDB para filtrar. Opcional.
     * @param gameModeId ID del modo de juego según IGDB para filtrar. Opcional.
     * @param limit Número máximo de resultados a devolver. Opcional (defecto 10, máximo 500).
     * @return Un {@link Flux} que emite objetos {@link GameDto} para cada juego que coincida con los filtros.
     * En caso de error en la llamada a la API o deserialización, el Flux emitirá un error.
     */
    public Flux<GameDto> findGamesByCustomFilter(
            Long releaseDateStart, Long releaseDateEnd,
            Integer genreId, Integer themeId, Integer gameModeId,
            Integer limit) {

        List<String> whereClauses = new ArrayList<>();

        if (releaseDateStart != null) {
            whereClauses.add("first_release_date >= " + releaseDateStart);
        }
        if (releaseDateEnd != null) {
            whereClauses.add("first_release_date < " + releaseDateEnd);
        }
        if (genreId != null) {
            whereClauses.add("genres = [" + genreId + "]");
        }
        if (themeId != null) {
            whereClauses.add("themes = [" + themeId + "]");
        }
        if (gameModeId != null) {
            whereClauses.add("game_modes = [" + gameModeId + "]");
        }

        String fields = "fields name, total_rating, cover.url, first_release_date, genres.name, themes.name, game_modes.name, id, slug;";
        String sortClause = "sort hypes desc;";
        String whereCondition = whereClauses.isEmpty() ? "" : "where " + String.join(" & ", whereClauses) + ";";

        int effectiveLimit = (limit != null && limit > 0) ? Math.min(limit, 500) : 300;
        String limitCondition = "limit " + effectiveLimit + ";";

        String queryBody = fields + " " + sortClause+ " "+ whereCondition + " " + limitCondition;

        logger.info("Querying IGDB (findGamesByCustomFilter) with body: {}", queryBody);

        return igdbWebClient.post()
                .uri("/games")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(queryBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    logger.error("Error from IGDB API (custom filter): Status {}, Body: {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("IGDB API Error (custom filter): " + clientResponse.statusCode() + " - " + errorBody));
                                }))
                .bodyToFlux(GameDto.class)
                .map(gameDto -> {

                    return gameDto;
                })
                .doOnError(error -> logger.error("Error during IGDB custom filter call or deserialization: {}", error.getMessage(), error));
    }

    /**
     * Busca los juegos lanzados en los últimos 30 días.
     * @return Un Flux de GameDto con los juegos lanzados recientemente.
     */
    public Flux<GameDto> findRecentlyReleasedGames() {
        long now = Instant.now().getEpochSecond();
        long thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS).getEpochSecond();
        String fields = "fields name, total_rating, cover.url, first_release_date, game_type, summary, id;";
        String queryBody = String.format(
                "%s sort first_release_date desc; where first_release_date < %d & first_release_date >= %d; limit 40;",
                fields, now, thirtyDaysAgo
        );

        logger.info("Querying IGDB (findRecentlyReleasedGames) with body: {}", queryBody);
        return igdbWebClient.post()
                .uri("/games")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(queryBody)
                .retrieve()
                .bodyToFlux(GameDto.class)
                .doOnError(error -> logger.error("Error during IGDB call (findRecentlyReleasedGames): {}", error.getMessage(), error));
    }

    /**
     * Busca los juegos más populares ("hyped") con un número significativo de calificaciones.
     * @return Un Flux de GameDto con los juegos más populares.
     */
    public Flux<GameDto> findMostHypedGames() {
        String fields = "fields name, total_rating, cover.url, first_release_date, game_type, summary, id;";
        String queryBody = String.format(
                "%s sort hypes desc; where total_rating != null & total_rating_count > 100; limit 40;",
                fields
        );

        logger.info("Querying IGDB (findMostHypedGames) with body: {}", queryBody);
        return igdbWebClient.post()
                .uri("/games")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(queryBody)
                .retrieve()
                .bodyToFlux(GameDto.class)
                .doOnError(error -> logger.error("Error during IGDB call (findMostHypedGames): {}", error.getMessage(), error));
    }

    /**
     * Busca los próximos lanzamientos más esperados ("highly anticipated").
     * @return Un Flux de GameDto con los próximos juegos más esperados.
     */
    public Flux<GameDto> findHighlyAnticipatedGames() {
        long now = Instant.now().getEpochSecond();
        String fields = "fields name, total_rating, cover.url, first_release_date, game_type, summary, id;";
        String queryBody = String.format(
                "%s sort hypes desc; where first_release_date > %d & hypes > 50; limit 40;",
                fields, now
        );

        logger.info("Querying IGDB (findHighlyAnticipatedGames) with body: {}", queryBody);
        return igdbWebClient.post()
                .uri("/games")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(queryBody)
                .retrieve()
                .bodyToFlux(GameDto.class)
                .doOnError(error -> logger.error("Error during IGDB call (findHighlyAnticipatedGames): {}", error.getMessage(), error));
    }

    /**
     * Busca los próximos 10 lanzamientos ordenados por fecha.
     * @return Un Flux de GameDto con los próximos lanzamientos.
     */
    public Flux<GameDto> findUpcomingReleases() {
        long now = Instant.now().getEpochSecond();
        String fields = "fields name, total_rating, cover.url, first_release_date, game_type, summary, id;";
        String queryBody = String.format(
                "%s sort first_release_date asc; where first_release_date > %d; limit 40;",
                fields, now
        );

        logger.info("Querying IGDB (findUpcomingReleases) with body: {}", queryBody);
        return igdbWebClient.post()
                .uri("/games")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(queryBody)
                .retrieve()
                .bodyToFlux(GameDto.class)
                .doOnError(error -> logger.error("Error during IGDB call (findUpcomingReleases): {}", error.getMessage(), error));
    }

    /**
     * Busca todos los géneros disponibles en IGDB.
     * @return Un Flux de GenreDto.
     */
    public Flux<GenreDto> findAllGenres() {
        String queryBody = "fields id, name; limit 100;";
        logger.info("Querying IGDB (findAllGenres) with body: {}", queryBody);
        return igdbWebClient.post()
                .uri("/genres")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(queryBody)
                .retrieve()
                .bodyToFlux(GenreDto.class)
                .doOnError(error -> logger.error("Error during IGDB call (findAllGenres): {}", error.getMessage(), error));
    }

    /**
     * Busca todos los temas disponibles en IGDB.
     * @return Un Flux de ThemeDto.
     */
    public Flux<ThemeDto> findAllThemes() {
        String queryBody = "fields id, name; limit 100;";
        logger.info("Querying IGDB (findAllThemes) with body: {}", queryBody);
        return igdbWebClient.post()
                .uri("/themes")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(queryBody)
                .retrieve()
                .bodyToFlux(ThemeDto.class)
                .doOnError(error -> logger.error("Error during IGDB call (findAllThemes): {}", error.getMessage(), error));
    }

    /**
     * Busca todos los modos de juego disponibles en IGDB.
     * @return Un Flux de GameModeDto.
     */
    public Flux<GameModeDto> findAllGameModes() {
        String queryBody = "fields id, name; limit 100;";
        logger.info("Querying IGDB (findAllGameModes) with body: {}", queryBody);
        return igdbWebClient.post()
                .uri("/game_modes")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(queryBody)
                .retrieve()
                .bodyToFlux(GameModeDto.class)
                .doOnError(error -> logger.error("Error during IGDB call (findAllGameModes): {}", error.getMessage(), error));
    }
}