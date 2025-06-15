package mp.tfg.mycheckpoint.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mp.tfg.mycheckpoint.dto.games.GameDto;

import mp.tfg.mycheckpoint.dto.games.GameModeDto;
import mp.tfg.mycheckpoint.dto.games.GenreDto;
import mp.tfg.mycheckpoint.dto.games.ThemeDto;
import mp.tfg.mycheckpoint.mapper.games.GameMapper;
import mp.tfg.mycheckpoint.service.games.GameService;
import mp.tfg.mycheckpoint.service.games.IgdbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;




/**
 * Controlador API para interactuar con información de juegos.
 * Actualmente, se enfoca en proporcionar endpoints para buscar y filtrar juegos
 * a través de la API de IGDB (Internet Game Database), permitiendo el acceso
 * público a esta información.
 */
@Tag(name = "Game Controller", description = "API para interactuar con información de juegos, principalmente a través de IGDB")
@RestController
@RequestMapping("/api/juegos")
public class GameController {

    private final GameService gameService;
    private final GameMapper gameMapper;
    private final IgdbService igdbService;
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    /**
     * Constructor para {@code GameController}.
     * Inyecta los servicios y mappers necesarios para la gestión y consulta de información de juegos.
     *
     * @param gameService Servicio para la lógica de negocio relacionada con juegos en la base de datos local.
     * @param gameMapper Mapper para convertir entre entidades de juego y DTOs.
     * @param igdbService Servicio para interactuar con la API de IGDB.
     */
    @Autowired
    public GameController(GameService gameService, GameMapper gameMapper, IgdbService igdbService) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
        this.igdbService = igdbService;
    }

    /**
     * Busca juegos en la API de IGDB basándose en un nombre o término de búsqueda.
     * Este endpoint es público y devuelve un flujo de objetos {@link GameDto} con información resumida
     * de los juegos encontrados (nombre, calificación, carátula, fecha de lanzamiento, tipo, resumen e ID).
     *
     * @param nombre El término de búsqueda para el nombre del juego. Es un parámetro de consulta obligatorio.
     * @return Un {@link Flux} de {@link GameDto} que emite los juegos encontrados.
     * Puede emitir un error si la API de IGDB devuelve un error o si hay problemas de deserialización.
     */
    @GetMapping("/igdb/buscar")
    @Operation(summary = "Buscar juegos en IGDB por nombre",
            description = "Realiza una búsqueda de juegos en la base de datos de IGDB utilizando un término de búsqueda para el nombre. " +
                    "Devuelve un flujo (o lista) de juegos que coinciden, con un conjunto limitado de campos (nombre, calificación, carátula, fecha de lanzamiento, tipo, resumen, ID). " +
                    "Este endpoint es público.",
            operationId = "buscarJuegosEnIgdb")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa. Devuelve una lista de juegos encontrados (puede estar vacía).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GameDto.class))
                    )),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta. El parámetro 'nombre' es obligatorio.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o error al comunicarse con la API de IGDB.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public Flux<GameDto> buscarJuegosEnIgdb(
            @Parameter(name = "nombre",
                    description = "Término de búsqueda para el nombre del juego.",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "Zelda",
                    schema = @Schema(type = "string"))
            @RequestParam String nombre) {
        return igdbService.findGamesByName(nombre)
                .map(gameDto -> {
                    return gameDto;
                });
    }

    /**
     * Filtra juegos en la API de IGDB utilizando múltiples criterios opcionales.
     * Los criterios incluyen rango de fechas de lanzamiento, ID de género, ID de tema, ID de modo de juego y un límite de resultados.
     * Este endpoint es público y devuelve un flujo de objetos {@link GameDto} con información de los juegos que coinciden.
     *
     * @param releaseDateStart Timestamp Unix (segundos) para la fecha de inicio del rango de lanzamiento. Opcional.
     * @param releaseDateEnd Timestamp Unix (segundos) para la fecha de fin del rango de lanzamiento. Opcional.
     * @param genreId ID del género (según IGDB) para filtrar. Opcional.
     * @param themeId ID del tema (según IGDB) para filtrar. Opcional.
     * @param gameModeId ID del modo de juego (según IGDB) para filtrar. Opcional.
     * @param limit Número máximo de resultados a devolver (por defecto 10, máximo 500). Opcional.
     * @return Un {@link Flux} de {@link GameDto} que emite los juegos filtrados.
     * Puede emitir un error si la API de IGDB devuelve un error o si hay problemas de deserialización.
     */
    @GetMapping("/igdb/filtrar")
    @Operation(summary = "Filtrar juegos en IGDB por múltiples criterios",
            description = "Permite buscar juegos en IGDB aplicando filtros opcionales como rango de fechas de lanzamiento, ID de género, ID de tema, y ID de modo de juego. " +
                    "Devuelve un flujo (o lista) de juegos que coinciden, con un conjunto limitado de campos. " +
                    "Este endpoint es público.",
            operationId = "filtrarJuegosEnIgdb")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda por filtros exitosa. Devuelve una lista de juegos encontrados (puede estar vacía).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GameDto.class))
                    )),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta. Ocurre si alguno de los parámetros numéricos no puede ser parseado correctamente (ej. texto en lugar de número).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o error al comunicarse con la API de IGDB.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public Flux<GameDto> filtrarJuegosEnIgdb(
            @Parameter(name = "fecha_inicio",
                    description = "Fecha de inicio del rango de lanzamiento (timestamp Unix en segundos). Opcional.",
                    in = ParameterIn.QUERY,
                    required = false,
                    example = "1420070400",
                    schema = @Schema(type = "integer", format = "int64"))
            @RequestParam(name = "fecha_inicio", required = false) Long releaseDateStart,

            @Parameter(name = "fecha_fin",
                    description = "Fecha de fin del rango de lanzamiento (timestamp Unix en segundos). Opcional.",
                    in = ParameterIn.QUERY,
                    required = false,
                    example = "1451606399",
                    schema = @Schema(type = "integer", format = "int64"))
            @RequestParam(name = "fecha_fin", required = false) Long releaseDateEnd,

            @Parameter(name = "id_genero",
                    description = "ID del género según IGDB para filtrar. Opcional.",
                    in = ParameterIn.QUERY,
                    required = false,
                    example = "12",
                    schema = @Schema(type = "integer", format = "int32"))
            @RequestParam(name = "id_genero", required = false) Integer genreId,

            @Parameter(name = "id_tema",
                    description = "ID del tema según IGDB para filtrar. Opcional.",
                    in = ParameterIn.QUERY,
                    required = false,
                    example = "1",
                    schema = @Schema(type = "integer", format = "int32"))
            @RequestParam(name = "id_tema", required = false) Integer themeId,

            @Parameter(name = "id_modo_juego",
                    description = "ID del modo de juego según IGDB para filtrar. Opcional.",
                    in = ParameterIn.QUERY,
                    required = false,
                    example = "1",
                    schema = @Schema(type = "integer", format = "int32"))
            @RequestParam(name = "id_modo_juego", required = false) Integer gameModeId,

            @Parameter(name = "limite",
                    description = "Número máximo de resultados a devolver. Opcional. El valor por defecto se gestiona en el servidor (ej. 20), máximo 500.",
                    in = ParameterIn.QUERY,
                    required = false,
                    example = "25",
                    schema = @Schema(type = "integer", format = "int32", minimum = "1", maximum = "500"))
            @RequestParam(name = "limite", required = false) Integer limit) {

        return igdbService.findGamesByCustomFilter(
                releaseDateStart, releaseDateEnd,
                genreId, themeId, gameModeId,
                limit
        );
    }

    @GetMapping("/igdb/recently-released")
    @Operation(summary = "Obtener juegos lanzados recientemente desde IGDB",
            description = "Recupera una lista de los 10 juegos lanzados en los últimos 30 días, ordenados por fecha de lanzamiento descendente.",
            operationId = "findRecentlyReleasedGames")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GameDto.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o error al comunicarse con la API de IGDB.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public Flux<GameDto> findRecentlyReleasedGames() {
        return igdbService.findRecentlyReleasedGames();
    }

    @GetMapping("/igdb/most-hyped")
    @Operation(summary = "Obtener los juegos más populares (hyped) desde IGDB",
            description = "Recupera una lista de los 10 juegos más populares basados en su 'hype' y un número significativo de calificaciones.",
            operationId = "findMostHypedGames")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GameDto.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o error al comunicarse con la API de IGDB.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public Flux<GameDto> findMostHypedGames() {
        return igdbService.findMostHypedGames();
    }

    @GetMapping("/igdb/highly-anticipated")
    @Operation(summary = "Obtener los próximos lanzamientos más esperados desde IGDB",
            description = "Recupera una lista de los 10 próximos lanzamientos más esperados, filtrados por su 'hype'.",
            operationId = "findHighlyAnticipatedGames")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GameDto.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o error al comunicarse con la API de IGDB.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public Flux<GameDto> findHighlyAnticipatedGames() {
        return igdbService.findHighlyAnticipatedGames();
    }

    @GetMapping("/igdb/upcoming-releases")
    @Operation(summary = "Obtener los próximos lanzamientos desde IGDB",
            description = "Recupera una lista de los 10 próximos lanzamientos, ordenados por fecha de lanzamiento ascendente.",
            operationId = "findUpcomingReleases")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GameDto.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o error al comunicarse con la API de IGDB.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public Flux<GameDto> findUpcomingReleases() {
        return igdbService.findUpcomingReleases();
    }

    @GetMapping("/igdb/genres")
    @Operation(summary = "Obtener todos los géneros de IGDB",
            description = "Recupera una lista de todos los géneros de juegos disponibles en IGDB.",
            operationId = "findAllGenres")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GenreDto.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o error al comunicarse con la API de IGDB.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public Flux<GenreDto> findAllGenres() {
        return igdbService.findAllGenres();
    }

    @GetMapping("/igdb/themes")
    @Operation(summary = "Obtener todos los temas de IGDB",
            description = "Recupera una lista de todos los temas de juegos disponibles en IGDB.",
            operationId = "findAllThemes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ThemeDto.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o error al comunicarse con la API de IGDB.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public Flux<ThemeDto> findAllThemes() {
        return igdbService.findAllThemes();
    }

    @GetMapping("/igdb/game-modes")
    @Operation(summary = "Obtener todos los modos de juego de IGDB",
            description = "Recupera una lista de todos los modos de juego disponibles en IGDB.",
            operationId = "findAllGameModes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GameModeDto.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o error al comunicarse con la API de IGDB.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public Flux<GameModeDto> findAllGameModes() {
        return igdbService.findAllGameModes();
    }

}