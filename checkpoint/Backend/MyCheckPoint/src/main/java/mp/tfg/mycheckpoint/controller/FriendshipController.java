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
import mp.tfg.mycheckpoint.dto.friendship.FriendshipResponseDTO;
import mp.tfg.mycheckpoint.security.UserDetailsImpl;
import mp.tfg.mycheckpoint.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador API para la gestión de amistades y solicitudes de amistad entre usuarios.
 * Ofrece endpoints para enviar, aceptar, rechazar o cancelar solicitudes de amistad,
 * así como para eliminar amigos existentes y listar amigos o solicitudes pendientes.
 * Todas las operaciones requieren autenticación.
 */
@Tag(name = "Friendship Controller", description = "API para la gestión de amistades y solicitudes de amistad")
@RestController
@RequestMapping("/api/v1/friends") // Base path para la funcionalidad de amigos
public class FriendshipController {

    private final FriendshipService friendshipService;

    /**
     * Constructor para {@code FriendshipController}.
     * Inyecta el servicio necesario para la lógica de negocio de las amistades.
     *
     * @param friendshipService El servicio para gestionar las operaciones de amistad.
     */
    @Autowired
    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    /**
     * Envía una solicitud de amistad desde el usuario autenticado a otro usuario.
     * Si ya existe una solicitud pendiente del receptor hacia el emisor, la amistad se aceptará automáticamente.
     *
     * @param currentUser El principal del usuario autenticado que envía la solicitud.
     * @param receiverUserPublicId El ID público (UUID) del usuario que recibirá la solicitud.
     * @return ResponseEntity con un {@link FriendshipResponseDTO} que representa el estado de la amistad/solicitud y el código HTTP 200 OK.
     */
    @PostMapping("/requests/send/{receiverUserPublicId}")
    @Operation(summary = "Enviar una solicitud de amistad",
            description = "Permite al usuario autenticado enviar una solicitud de amistad a otro usuario especificado por su ID público. " +
                    "Si ya existe una solicitud pendiente del receptor hacia el emisor, la amistad se aceptará automáticamente. " +
                    "Requiere autenticación.",
            operationId = "sendFriendRequest",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud de amistad enviada o amistad auto-aceptada exitosamente. Devuelve el estado de la amistad/solicitud.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FriendshipResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta. El usuario no puede enviarse una solicitud a sí mismo.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "401", description = "No autorizado. El token JWT es inválido, ha expirado o no se proporcionó.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. El usuario receptor especificado por `receiverUserPublicId` no existe, o el usuario emisor no pudo ser verificado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "409", description = "Conflicto. Ya existe una amistad o una solicitud de amistad pendiente con este usuario.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/DuplicatedResourceResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<FriendshipResponseDTO> sendFriendRequest(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Parameter(name = "receiverUserPublicId",
                    description = "ID público (UUID) del usuario al que se le envía la solicitud de amistad.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                    schema = @Schema(type = "string", format = "uuid"))
            @PathVariable UUID receiverUserPublicId) {
        FriendshipResponseDTO response = friendshipService.sendFriendRequest(currentUser.getEmail(), receiverUserPublicId);
        return ResponseEntity.ok(response);
    }

    /**
     * Acepta una solicitud de amistad pendiente.
     * El usuario autenticado (receptor) acepta la solicitud enviada por otro usuario.
     *
     * @param currentUser El principal del usuario autenticado que acepta la solicitud.
     * @param requesterUserPublicId El ID público (UUID) del usuario que envió la solicitud de amistad.
     * @return ResponseEntity con un {@link FriendshipResponseDTO} que representa la amistad aceptada y el código HTTP 200 OK.
     */
    @PutMapping("/requests/accept/{requesterUserPublicId}")
    @Operation(summary = "Aceptar una solicitud de amistad pendiente",
            description = "Permite al usuario autenticado (que es el receptor de la solicitud) aceptar una solicitud de amistad pendiente de otro usuario. " +
                    "La solicitud debe estar en estado PENDIENTE. Requiere autenticación.",
            operationId = "acceptFriendRequest",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud de amistad aceptada exitosamente. Devuelve el estado actualizado de la amistad.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FriendshipResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado. El token JWT es inválido, ha expirado o no se proporcionó.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))),
            @ApiResponse(responseCode = "403", description = "Prohibido. El usuario autenticado no es el receptor de la solicitud de amistad pendiente o no tiene permisos para realizar esta acción.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. No se encontró una solicitud de amistad pendiente del usuario especificado, o el usuario solicitante/actual no existe.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<FriendshipResponseDTO> acceptFriendRequest(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Parameter(name = "requesterUserPublicId",
                    description = "ID público (UUID) del usuario que envió la solicitud de amistad.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "c4d5e6f7-g8h9-0123-4567-890abcdef12",
                    schema = @Schema(type = "string", format = "uuid"))
            @PathVariable UUID requesterUserPublicId) {
        FriendshipResponseDTO response = friendshipService.acceptFriendRequest(currentUser.getEmail(), requesterUserPublicId);
        return ResponseEntity.ok(response);
    }

    /**
     * Rechaza o cancela una solicitud de amistad pendiente.
     * Si el usuario autenticado es el receptor, la rechaza. Si es el emisor, la cancela.
     * La solicitud se elimina de la base de datos.
     *
     * @param currentUser El principal del usuario autenticado que realiza la acción.
     * @param requesterUserPublicId El ID público (UUID) del usuario que envió originalmente la solicitud.
     * @return ResponseEntity con código HTTP 204 No Content si la operación es exitosa.
     */
    @DeleteMapping("/requests/decline/{requesterUserPublicId}")
    @Operation(summary = "Rechazar o cancelar una solicitud de amistad pendiente",
            description = "Permite al usuario autenticado (que es el receptor de la solicitud) rechazar una solicitud de amistad pendiente. " +
                    "Alternativamente, si el usuario autenticado fue quien envió la solicitud y esta aún está pendiente, puede usar este endpoint para cancelarla (aunque semánticamente esto último podría ser un endpoint diferente, la lógica actual del servicio elimina la solicitud PENDIENTE). " +
                    "La solicitud de amistad es eliminada de la base de datos. Requiere autenticación.",
            operationId = "declineOrCancelFriendRequest",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Solicitud de amistad rechazada/cancelada y eliminada exitosamente. No hay contenido en la respuesta."),
            @ApiResponse(responseCode = "401", description = "No autorizado. El token JWT es inválido, ha expirado o no se proporcionó.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))),
            @ApiResponse(responseCode = "403", description = "Prohibido. El usuario autenticado no es el receptor de la solicitud de amistad pendiente que intenta rechazar (o no tiene permisos para cancelarla si fuera el emisor y el endpoint se usara así).",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. No se encontró una solicitud de amistad pendiente del usuario especificado, o el usuario solicitante/actual no existe.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<Void> declineFriendRequest(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Parameter(name = "requesterUserPublicId",
                    description = "ID público (UUID) del usuario que originalmente envió la solicitud de amistad que se desea rechazar/cancelar.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "d7e8f9a0-b1c2-3456-7890-abcdef123456",
                    schema = @Schema(type = "string", format = "uuid"))
            @PathVariable UUID requesterUserPublicId) {
        friendshipService.declineFriendRequest(currentUser.getEmail(), requesterUserPublicId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina una amistad existente entre el usuario autenticado y otro usuario.
     *
     * @param currentUser El principal del usuario autenticado que elimina la amistad.
     * @param friendUserPublicId El ID público (UUID) del amigo a eliminar.
     * @return ResponseEntity con código HTTP 204 No Content si la operación es exitosa.
     */
    @DeleteMapping("/{friendUserPublicId}")
    @Operation(summary = "Eliminar un amigo",
            description = "Permite al usuario autenticado eliminar una amistad existente con otro usuario, especificado por su ID público. " +
                    "La relación de amistad es eliminada de la base de datos. Requiere autenticación.",
            operationId = "removeFriend",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Amigo eliminado exitosamente. No hay contenido en la respuesta."),
            @ApiResponse(responseCode = "401", description = "No autorizado. El token JWT es inválido, ha expirado o no se proporcionó.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. No se encontró una amistad con el usuario especificado, o el amigo a eliminar no existe, o el usuario actual no pudo ser verificado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<Void> removeFriend(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Parameter(name = "friendUserPublicId",
                    description = "ID público (UUID) del amigo que se desea eliminar.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "e0f1a2b3-c4d5-6789-0123-abcdef123456",
                    schema = @Schema(type = "string", format = "uuid"))
            @PathVariable UUID friendUserPublicId) {
        friendshipService.removeFriend(currentUser.getEmail(), friendUserPublicId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene la lista de todos los amigos aceptados del usuario autenticado.
     *
     * @param currentUser El principal del usuario autenticado.
     * @return ResponseEntity con una lista de {@link FriendshipResponseDTO} que representan a los amigos y el código HTTP 200 OK.
     * La lista puede estar vacía.
     */
    @GetMapping
    @Operation(summary = "Obtener la lista de amigos del usuario autenticado",
            description = "Recupera una lista de todos los usuarios que son amigos del usuario actualmente autenticado (es decir, aquellas relaciones con estado 'ACCEPTED'). " +
                    "Requiere autenticación.",
            operationId = "getMyFriends",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de amigos recuperada exitosamente. La lista puede estar vacía si el usuario no tiene amigos.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = FriendshipResponseDTO.class))
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
    public ResponseEntity<List<FriendshipResponseDTO>> getMyFriends(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<FriendshipResponseDTO> friends = friendshipService.getFriends(currentUser.getEmail());
        return ResponseEntity.ok(friends);
    }

    /**
     * Obtiene todas las solicitudes de amistad pendientes que el usuario autenticado ha recibido.
     *
     * @param currentUser El principal del usuario autenticado.
     * @return ResponseEntity con una lista de {@link FriendshipResponseDTO} que representan las solicitudes recibidas y el código HTTP 200 OK.
     * La lista puede estar vacía.
     */
    @GetMapping("/requests/received")
    @Operation(summary = "Obtener las solicitudes de amistad pendientes recibidas por el usuario autenticado",
            description = "Recupera una lista de todas las solicitudes de amistad que el usuario actualmente autenticado ha recibido y aún están pendientes de acción (aceptar o rechazar). " +
                    "Requiere autenticación.",
            operationId = "getPendingRequestsReceived",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitudes pendientes recibidas recuperada exitosamente. La lista puede estar vacía.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = FriendshipResponseDTO.class))
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
    public ResponseEntity<List<FriendshipResponseDTO>> getPendingRequestsReceived(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<FriendshipResponseDTO> requests = friendshipService.getPendingRequestsReceived(currentUser.getEmail());
        return ResponseEntity.ok(requests);
    }

    /**
     * Obtiene todas las solicitudes de amistad pendientes que el usuario autenticado ha enviado.
     *
     * @param currentUser El principal del usuario autenticado.
     * @return ResponseEntity con una lista de {@link FriendshipResponseDTO} que representan las solicitudes enviadas y el código HTTP 200 OK.
     * La lista puede estar vacía.
     */
    @GetMapping("/requests/sent")
    @Operation(summary = "Obtener las solicitudes de amistad pendientes enviadas por el usuario autenticado",
            description = "Recupera una lista de todas las solicitudes de amistad que el usuario actualmente autenticado ha enviado y que aún están pendientes de respuesta por parte de los destinatarios. " +
                    "Requiere autenticación.",
            operationId = "getPendingRequestsSent",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitudes pendientes enviadas recuperada exitosamente. La lista puede estar vacía.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = FriendshipResponseDTO.class))
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
    public ResponseEntity<List<FriendshipResponseDTO>> getPendingRequestsSent(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<FriendshipResponseDTO> requests = friendshipService.getPendingRequestsSent(currentUser.getEmail());
        return ResponseEntity.ok(requests);
    }
}
