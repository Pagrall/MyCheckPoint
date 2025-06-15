package mp.tfg.mycheckpoint.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mp.tfg.mycheckpoint.dto.usergame.GameDetailDTO;
import mp.tfg.mycheckpoint.dto.usergame.UserGameDataDTO;
import mp.tfg.mycheckpoint.dto.usergame.UserGameResponseDTO;
import mp.tfg.mycheckpoint.security.UserDetailsImpl;
import mp.tfg.mycheckpoint.service.UserGameLibraryService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador API para gestionar la biblioteca de juegos personal de un usuario.
 * Proporciona endpoints para añadir, actualizar, obtener y eliminar juegos de la
 * biblioteca del usuario autenticado, así como para ver detalles completos de un juego
 * que pueden incluir datos personalizados del usuario y comentarios públicos.
 */
@Tag(name = "User Game Library Controller", description = "API para gestionar la biblioteca de juegos personal de un usuario")
@RestController
@RequestMapping("/api/v1") // Base path general
public class UserGameLibraryController {

    private final UserGameLibraryService userGameLibraryService;

    /**
     * Constructor para {@code UserGameLibraryController}.
     * Inyecta el servicio necesario para la lógica de negocio de la biblioteca de juegos del usuario.
     *
     * @param userGameLibraryService El servicio para gestionar las operaciones de la biblioteca de juegos.
     */
    @Autowired
    public UserGameLibraryController(UserGameLibraryService userGameLibraryService) {
        this.userGameLibraryService = userGameLibraryService;
    }

    /**
     * Añade un juego a la biblioteca del usuario autenticado o actualiza una entrada existente.
     * Si el juego no existe en la base de datos local, se intentará obtener de IGDB.
     * Se proporcionan datos específicos del usuario para este juego (estado, puntuación, plataforma, etc.).
     *
     * @param currentUser El principal del usuario autenticado.
     * @param igdbId El ID de IGDB del juego a añadir o actualizar.
     * @param userGameDataDTO DTO que contiene los datos específicos del usuario para este juego.
     * @return ResponseEntity con un {@link UserGameResponseDTO} que representa la entrada de la biblioteca actualizada o creada y el código HTTP 200 OK.
     */
    // Endpoint para añadir o actualizar un juego en la biblioteca del usuario autenticado
    @PostMapping("/users/me/library/games/{igdbId}")
    @Operation(summary = "Añadir o actualizar un juego en la biblioteca del usuario autenticado",
            description = "Permite al usuario autenticado añadir un juego (identificado por su IGDB ID) a su biblioteca personal o actualizar una entrada existente. " +
                    "Si el juego no existe en la base de datos local, se intentará obtener de IGDB. " +
                    "Se proporcionan datos específicos del usuario para este juego (estado, puntuación, plataforma, etc.). Requiere autenticación.",
            operationId = "addOrUpdateGameInMyLibrary",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego añadido o actualizado en la biblioteca exitosamente. Devuelve la entrada de la biblioteca actualizada.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserGameResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos. Ocurre si los datos en `UserGameDataDTO` no pasan las validaciones (ej. puntuación fuera de rango).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))),
            @ApiResponse(responseCode = "401", description = "No autorizado. El token JWT es inválido, ha expirado o no se proporcionó.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. El usuario autenticado no pudo ser verificado, o el juego con el `igdbId` proporcionado no se encontró en IGDB.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor. Podría ocurrir si hay problemas al contactar IGDB o al guardar los datos.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<UserGameResponseDTO> addOrUpdateGameInMyLibrary(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Parameter(name = "igdbId",
                    description = "ID de IGDB del juego a añadir o actualizar en la biblioteca.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1020",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long igdbId,
            @Valid @org.springframework.web.bind.annotation.RequestBody UserGameDataDTO userGameDataDTO){
        UserGameResponseDTO response = userGameLibraryService.addOrUpdateGameInLibrary(currentUser.getEmail(), igdbId, userGameDataDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene la biblioteca completa de juegos del usuario autenticado.
     * Recupera todas las entradas de juegos, incluyendo estado, puntuación, plataforma y otros datos específicos del usuario.
     *
     * @param currentUser El principal del usuario autenticado.
     * @return ResponseEntity con una lista de {@link UserGameResponseDTO} representando la biblioteca del usuario y el código HTTP 200 OK.
     * La lista puede estar vacía.
     */
    // Endpoint para obtener toda la biblioteca del usuario autenticado
    @GetMapping("/users/me/library/games")
    @Operation(summary = "Obtener la biblioteca completa de juegos del usuario autenticado",
            description = "Recupera todas las entradas de juegos que el usuario actualmente autenticado tiene en su biblioteca personal, " +
                    "incluyendo el estado, puntuación, plataforma y otros datos específicos del usuario para cada juego. Requiere autenticación.",
            operationId = "getMyGameLibrary",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Biblioteca de juegos recuperada exitosamente. La lista puede estar vacía si el usuario no tiene juegos añadidos.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = UserGameResponseDTO.class))
                    )),
            @ApiResponse(responseCode = "401", description = "No autorizado. El token JWT es inválido, ha expirado o no se proporcionó.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. El usuario autenticado no pudo ser verificado en la base de datos (caso anómalo).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<List<UserGameResponseDTO>> getMyGameLibrary(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<UserGameResponseDTO> library = userGameLibraryService.getUserGameLibrary(currentUser.getEmail());
        return ResponseEntity.ok(library);
    }

    /**
     * Obtiene los detalles de un juego específico de la biblioteca del usuario autenticado.
     *
     * @param currentUser El principal del usuario autenticado.
     * @param igdbId El ID de IGDB del juego a obtener de la biblioteca.
     * @return ResponseEntity con un {@link UserGameResponseDTO} con los datos del juego en la biblioteca del usuario y el código HTTP 200 OK.
     */
    // Endpoint para obtener un juego específico de la biblioteca del usuario autenticado
    @GetMapping("/users/me/library/games/{igdbId}")
    @Operation(summary = "Obtener un juego específico de la biblioteca del usuario autenticado",
            description = "Recupera los detalles de un juego específico (identificado por su IGDB ID) tal como existe en la biblioteca personal del usuario autenticado. " +
                    "Esto incluye el estado, puntuación, y otros datos que el usuario haya registrado para ese juego. Requiere autenticación.",
            operationId = "getSpecificGameFromMyLibrary",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego específico de la biblioteca recuperado exitosamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserGameResponseDTO.class)
                    )),
            @ApiResponse(responseCode = "401", description = "No autorizado. El token JWT es inválido, ha expirado o no se proporcionó.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. El juego con el IGDB ID especificado no se encontró en la biblioteca del usuario, o el usuario autenticado no pudo ser verificado.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<UserGameResponseDTO> getSpecificGameFromMyLibrary(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Parameter(name = "igdbId",
                    description = "ID de IGDB del juego a obtener de la biblioteca del usuario.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1020",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long igdbId) {
        UserGameResponseDTO gameFromLibrary = userGameLibraryService.getUserGameFromLibrary(currentUser.getEmail(), igdbId);
        return ResponseEntity.ok(gameFromLibrary);
    }

    /**
     * Obtiene detalles completos de un juego, combinando información general, datos del usuario y comentarios públicos.
     * Este endpoint es público. Si se proporciona un token JWT de autenticación válido, la respuesta incluirá
     * adicionalmente los datos específicos del usuario para ese juego (si existen en su biblioteca).
     *
     * @param igdbId El ID de IGDB del juego para el cual se solicitan los detalles.
     * @param authentication Objeto de autenticación de Spring Security, puede ser nulo si el acceso es anónimo.
     * @return ResponseEntity con un {@link GameDetailDTO} conteniendo los detalles completos del juego y el código HTTP 200 OK.
     */
    @GetMapping("/games/{igdbId}/details")
    @Operation(summary = "Obtener detalles completos de un juego",
            description = "Recupera información detallada sobre un juego específico, identificado por su IGDB ID. " +
                    "Este endpoint es público. Si se proporciona un token JWT de autenticación válido, la respuesta incluirá " +
                    "adicionalmente los datos específicos del usuario para ese juego (si existen en su biblioteca), como su estado, puntuación, etc. " +
                    "Si no se proporciona autenticación o el token es inválido, solo se devolverá la información pública del juego y los comentarios públicos.",
            operationId = "getGameDetails"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles del juego recuperados exitosamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GameDetailDTO.class))),
            @ApiResponse(responseCode = "404", description = "No encontrado. El juego con el IGDB ID especificado no se encontró o el usuario (si está autenticado) no pudo ser verificado.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor. Podría ocurrir si hay problemas al contactar IGDB.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<GameDetailDTO> getGameDetails(
            @Parameter(name = "igdbId",
                    description = "ID de IGDB del juego para el cual se solicitan los detalles.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1020",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long igdbId,
            Authentication authentication) {

        String userEmail = null;
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();
            userEmail = currentUser.getEmail();
        }

        GameDetailDTO gameDetail = userGameLibraryService.getGameDetailsForUser(igdbId, userEmail);
        return ResponseEntity.ok(gameDetail);
    }


    /**
     * Elimina un juego de la biblioteca personal del usuario autenticado.
     *
     * @param currentUser El principal del usuario autenticado.
     * @param igdbId El ID de IGDB del juego a eliminar de la biblioteca.
     * @return ResponseEntity con código HTTP 204 No Content si la operación es exitosa.
     */
    // Endpoint para eliminar un juego de la biblioteca del usuario autenticado
    @DeleteMapping("/users/me/library/games/{igdbId}")
    @Operation(summary = "Eliminar un juego de la biblioteca del usuario autenticado",
            description = "Permite al usuario autenticado eliminar un juego específico (identificado por su IGDB ID) de su biblioteca personal. " +
                    "Requiere autenticación.",
            operationId = "removeGameFromMyLibrary",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Juego eliminado de la biblioteca exitosamente. No hay contenido en la respuesta."),
            @ApiResponse(responseCode = "401", description = "No autorizado. El token JWT es inválido, ha expirado o no se proporcionó.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. El juego con el IGDB ID especificado no se encontró en la biblioteca del usuario para eliminar, o el usuario autenticado no pudo ser verificado.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<Void> removeGameFromMyLibrary(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Parameter(name = "igdbId",
                    description = "ID de IGDB del juego a eliminar de la biblioteca del usuario.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "1020",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long igdbId) {
        userGameLibraryService.removeGameFromLibrary(currentUser.getEmail(), igdbId);
        return ResponseEntity.noContent().build();
    }

    /**
     *  Obtiene la biblioteca de juegos pública de un usuario específico por su ID público.
     *
     *  @param publicId El ID público (UUID) del usuario.
     *  @return ResponseEntity con una lista de {@link UserGameResponseDTO} representando la biblioteca pública del usuario.
     *
     */
    @GetMapping("/users/public/{publicId}/library")
    @Operation(summary = "Obtener la biblioteca de un usuario por su ID público",
            description = "Recupera la biblioteca de juegos de un usuario específico, sujeto a los permisos de visibilidad del perfil (PÚBLICO o SOLO_AMIGOS). " +
                    "Si el perfil es SOLO_AMIGOS, se requiere autenticación para verificar la amistad. Este endpoint es público.",
            operationId = "getPublicUserLibrary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Biblioteca recuperada exitosamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = UserGameResponseDTO.class)))),
            @ApiResponse(responseCode = "403", description = "Prohibido. La biblioteca del usuario es privada o solo para amigos y no eres amigo.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. El usuario con el ID público especificado no existe.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<List<UserGameResponseDTO>> getPublicUserLibrary(
            @Parameter(name = "publicId", description = "ID público (UUID) del usuario cuya biblioteca se desea obtener.", required = true, in = ParameterIn.PATH, example = "123e4567-e89b-12d3-a456-426614174000", schema = @Schema(type = "string", format = "uuid"))
            @PathVariable UUID publicId,
            Authentication authentication) {
        String currentUserEmail = null;
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetailsImpl) {
            currentUserEmail = ((UserDetailsImpl) authentication.getPrincipal()).getEmail();
        }
        List<UserGameResponseDTO> library = userGameLibraryService.getPublicUserGameLibrary(publicId, currentUserEmail);
        return ResponseEntity.ok(library);
    }
}
