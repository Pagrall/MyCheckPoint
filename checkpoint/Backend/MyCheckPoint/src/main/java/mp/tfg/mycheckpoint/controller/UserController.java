package mp.tfg.mycheckpoint.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mp.tfg.mycheckpoint.dto.user.*;
import mp.tfg.mycheckpoint.exception.ResourceNotFoundException;
import mp.tfg.mycheckpoint.security.UserDetailsImpl;
import mp.tfg.mycheckpoint.service.UserService;
import mp.tfg.mycheckpoint.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // IMPORTAR

import java.util.List;
import java.util.UUID;


/**
 * Controlador API para la gestión de usuarios.
 * Ofrece endpoints para el registro de nuevos usuarios, obtención de información
 * de perfiles (públicos y del usuario autenticado), actualización del perfil,
 * cambio de contraseña, gestión de la eliminación de cuenta, búsqueda de usuarios
 * y subida de fotos de perfil.
 */
@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios")
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

    /**
     * Constructor para {@code UserController}.
     * Inyecta los servicios necesarios para la lógica de negocio relacionada con
     * usuarios y el almacenamiento de archivos (para fotos de perfil).
     *
     * @param userService El servicio para gestionar las operaciones de usuario.
     * @param fileStorageService El servicio para gestionar el almacenamiento de archivos.
     */
    @Autowired
    public UserController(UserService userService, FileStorageService fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Tras el registro exitoso, se enviará un correo electrónico de verificación
     * a la dirección proporcionada para activar la cuenta.
     *
     * @param userCreateDTO DTO que contiene los datos del nuevo usuario a registrar (nombre de usuario, email, contraseña).
     * @return ResponseEntity con un {@link UserDTO} representando al usuario recién creado y el código HTTP 201 Created.
     */
    @PostMapping
    @Operation(summary = "Registrar un nuevo usuario",
            description = "Crea una nueva cuenta de usuario en el sistema. Tras el registro exitoso, se enviará un correo electrónico de verificación a la dirección proporcionada para activar la cuenta.",
            operationId = "registrarUsuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente. Devuelve los datos del usuario recién creado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos. Ocurre si los datos proporcionados en `UserCreateDTO` no pasan las validaciones (ej. email no válido, contraseña corta).",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))),
            @ApiResponse(responseCode = "409", description = "Conflicto. El email o el nombre de usuario proporcionado ya se encuentra registrado en el sistema.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/DuplicatedResourceResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor. Ocurrió un problema inesperado durante el proceso de registro.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<UserDTO> registrarUsuario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nuevo usuario a registrar. Todos los campos son obligatorios.", required = true,
                    content = @Content(schema = @Schema(implementation = UserCreateDTO.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody UserCreateDTO userCreateDTO) {
        UserDTO createdUser = userService.createUser(userCreateDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Obtiene un usuario por su ID interno.
     * Este endpoint está oculto en la documentación de Swagger y es probablemente para uso interno o de desarrollo.
     *
     * @param id El ID interno (Long) del usuario a obtener.
     * @return ResponseEntity con un {@link UserDTO} si se encuentra el usuario y el código HTTP 200 OK.
     * @throws ResourceNotFoundException si no se encuentra un usuario con el ID proporcionado.
     */
    @Hidden
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUsuarioById(
            @PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID interno: " + id));
    }

    /**
     * Obtiene un usuario por su ID público (UUID).
     * Este endpoint es público y no requiere autenticación.
     *
     * @param publicId El ID público (UUID) del usuario a obtener.
     * @return ResponseEntity con un {@link UserDTO} si se encuentra el usuario y el código HTTP 200 OK.
     * @throws ResourceNotFoundException si no se encuentra un usuario con el ID público proporcionado.
     */
    @GetMapping("/public/{publicId}")
    @Operation(summary = "Obtener un usuario por su ID público",
            description = "Recupera los detalles de un usuario específico utilizando su ID público (UUID). Este endpoint es público y no requiere autenticación.",
            operationId = "getUsuarioByPublicId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado y devuelto exitosamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "No encontrado. No existe ningún usuario con el ID público proporcionado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<UserDTO> getUsuarioByPublicId(
            @Parameter(name = "publicId",
                    description = "ID público (UUID) del usuario a obtener.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    schema = @Schema(type = "string", format = "uuid"))
            @PathVariable UUID publicId) {
        return userService.getUserByPublicId(publicId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con Public ID: " + publicId));
    }

    /**
     * Obtiene un usuario por su ID público (UUID) como un DTO de resultado de búsqueda.
     * Este endpoint es público y no requiere autenticación.
     *
     * @param publicId El ID público (UUID) del usuario a obtener.
     * @return ResponseEntity con un {@link UserSearchResultDTO} si se encuentra el usuario y el código HTTP 200 OK.
     * @throws ResourceNotFoundException si no se encuentra un usuario con el ID público proporcionado.
     */

    @GetMapping("/public/summary/{publicId}")
    @Operation(summary = "Obtener un resumen de usuario por su ID público",
            description = "Recupera un resumen de información pública de un usuario específico utilizando su ID público (UUID). Este endpoint es público y no requiere autenticación.",
            operationId = "getUsuarioSummaryByPublicId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumen de usuario encontrado y devuelto exitosamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSearchResultDTO.class))),
            @ApiResponse(responseCode = "404", description = "No encontrado. No existe ningún usuario con el ID público proporcionado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<UserSearchResultDTO> getUsuarioSummaryByPublicId(
            @Parameter(name = "publicId",
                    description = "ID público (UUID) del usuario a obtener.",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    schema = @Schema(type = "string", format = "uuid"))
            @PathVariable UUID publicId) {
        return userService.getUserByPublicIdAsSearchResult(publicId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (resumen) no encontrado con Public ID: " + publicId));
    }

    /**
     * Obtiene los datos del perfil del usuario actualmente autenticado.
     * Requiere un token JWT válido en la cabecera de autorización.
     *
     * @return ResponseEntity con un {@link UserDTO} conteniendo los datos del usuario autenticado y el código HTTP 200 OK.
     * @throws ResourceNotFoundException si el usuario autenticado (identificado por el token) no se encuentra en la base de datos.
     */
    @GetMapping("/me")
    @Operation(summary = "Obtener los datos del usuario autenticado actualmente",
            description = "Recupera los detalles del perfil y preferencias del usuario que ha iniciado sesión. Requiere un token JWT válido en la cabecera de autorización.",
            operationId = "getCurrentAuthenticatedUser",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos del usuario autenticado devueltos exitosamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado. El token JWT es inválido, ha expirado o no se proporcionó, o el contexto de seguridad no contiene un principal válido.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. El usuario autenticado (identificado por el token) no pudo ser encontrado en la base de datos.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<UserDTO> getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String userEmail = userDetails.getEmail();
        return userService.getUserByEmail(userEmail)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado en la base de datos con email: " + userEmail));
    }

    /**
     * Actualiza el perfil del usuario actualmente autenticado.
     * Permite modificar detalles como nombre de usuario, tema, foto de perfil (URL),
     * preferencias de notificación y visibilidad del perfil.
     * Solo los campos proporcionados en el DTO serán actualizados.
     *
     * @param currentUser El principal del usuario autenticado.
     * @param profileUpdateDTO DTO con los datos del perfil a actualizar.
     * @return ResponseEntity con un {@link UserDTO} representando el perfil actualizado y el código HTTP 200 OK.
     */
    @PutMapping("/me")
    @Operation(summary = "Actualizar el perfil del usuario autenticado actualmente",
            description = "Permite al usuario autenticado modificar los detalles de su perfil, como el nombre de usuario, tema, foto de perfil, preferencias de notificación y visibilidad del perfil. Requiere un token JWT válido.",
            operationId = "updateCurrentUserProfile",
            security = { @SecurityRequirement(name = "bearerAuth") },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del perfil del usuario a actualizar. Solo se actualizarán los campos proporcionados.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserProfileUpdateDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil de usuario actualizado exitosamente. Devuelve los datos actualizados del usuario.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos. Ocurre si los datos proporcionados en `UserProfileUpdateDTO` no pasan las validaciones (ej. nombre de usuario demasiado corto/largo).",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))),
            @ApiResponse(responseCode = "401", description = "No autorizado. El token JWT es inválido, ha expirado o no se proporcionó.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. El usuario autenticado (identificado por el token) no pudo ser encontrado en la base de datos para la actualización.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "409", description = "Conflicto. El nuevo nombre de usuario elegido ya está en uso por otro usuario.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/DuplicatedResourceResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<UserDTO> updateCurrentUserProfile(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @org.springframework.web.bind.annotation.RequestBody UserProfileUpdateDTO profileUpdateDTO) {
        UserDTO updatedUser = userService.updateUserProfile(currentUser.getEmail(), profileUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Sube o actualiza la foto de perfil del usuario autenticado.
     * El archivo de imagen debe cumplir con los formatos y límites de tamaño configurados.
     * Si ya existe una foto, será reemplazada.
     *
     * @param currentUser El principal del usuario autenticado.
     * @param file El archivo de imagen (MultipartFile) a subir como foto de perfil.
     * @return ResponseEntity con un {@link UserDTO} representando el perfil del usuario con la foto actualizada y el código HTTP 200 OK.
     */
    @PostMapping(value="/me/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir o actualizar la foto de perfil del usuario autenticado",
            description = "Permite al usuario autenticado subir un nuevo archivo de imagen para su foto de perfil. " +
                    "El archivo debe ser de un formato permitido (JPEG, PNG, GIF) y no exceder el tamaño máximo configurado. " +
                    "Si ya existe una foto de perfil, será reemplazada. Requiere autenticación.",
            operationId = "uploadProfilePicture",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto de perfil subida y perfil actualizado exitosamente. Devuelve los datos actualizados del usuario.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta. El archivo proporcionado está vacío, tiene un formato no permitido, o hay un problema con el nombre del archivo.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "401", description = "No autorizado. El token JWT es inválido, ha expirado o no se proporcionó.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. El usuario autenticado no pudo ser encontrado en la base de datos (caso anómalo).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "413", description = "Payload Too Large. El archivo excede el tamaño máximo permitido para fotos de perfil o el límite general de subida.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/TooLargeResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor. No se pudo crear el directorio de almacenamiento, guardar el archivo, o ocurrió otro error inesperado.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<UserDTO> uploadProfilePicture(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Parameter(
                    name = "file",
                    description = "El archivo de imagen a subir como foto de perfil.",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file) {

        String userPublicId = currentUser.getPublicId().toString();
        String fileName = fileStorageService.storeProfilePicture(file, userPublicId);
        UserDTO updatedUser = userService.updateUserProfilePicture(currentUser.getEmail(), fileName);

        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Cambia la contraseña del usuario autenticado.
     * Se requiere la contraseña actual para verificación.
     *
     * @param currentUser El principal del usuario autenticado.
     * @param passwordChangeDTO DTO que contiene la contraseña actual y la nueva contraseña deseada.
     * @return ResponseEntity con un mensaje de éxito y código HTTP 200 OK, o un mensaje de error
     * y el código HTTP correspondiente (401 Unauthorized, 400 Bad Request) en caso de fallo.
     */
    @PutMapping(value = "/me/password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cambiar la contraseña del usuario autenticado actualmente",
            description = "Permite al usuario autenticado cambiar su contraseña actual por una nueva. Se requiere la contraseña actual para la verificación. Requiere un token JWT válido.",
            operationId = "changeMyPassword",
            security = { @SecurityRequirement(name = "bearerAuth") },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "DTO con la contraseña actual y la nueva contraseña.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PasswordChangeDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object", properties = {}),
                            examples = @ExampleObject(name = "RespuestaExitosaCambioPass", value = "{\"message\": \"Contraseña actualizada correctamente.\"}")
                    )),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o solicitud incorrecta (ej. nueva contraseña igual a la actual).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ValidationPasswordErrorResponse")
                    )),
            @ApiResponse(responseCode = "401", description = "No autorizado. La contraseña actual proporcionada es incorrecta, o el token JWT es inválido/expirado.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. El usuario autenticado no pudo ser encontrado en la base de datos (caso anómalo).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<Object> changeMyPassword(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @org.springframework.web.bind.annotation.RequestBody PasswordChangeDTO passwordChangeDTO) {
        try {
            userService.changePassword(currentUser.getEmail(), passwordChangeDTO);
            return ResponseEntity.ok().body(java.util.Collections.singletonMap("message", "Contraseña actualizada correctamente."));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Collections.singletonMap("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) { // Considerar manejar ResourceNotFoundException específicamente si es esperada.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Collections.singletonMap("error", "Ocurrió un error inesperado al cambiar la contraseña."));
        }
    }

    /**
     * Programa la eliminación de la cuenta del usuario autenticado.
     * Se requiere la contraseña actual para confirmación. La cuenta se marcará para eliminación
     * y se borrará permanentemente después de un período de gracia.
     * La sesión actual del usuario se invalidará tras esta operación.
     *
     * @param currentUser El principal del usuario autenticado.
     * @param accountDeleteDTO DTO que contiene la contraseña actual del usuario para confirmar la eliminación.
     * @return ResponseEntity con un mensaje de éxito y código HTTP 200 OK si la solicitud es procesada,
     * o un mensaje de error y el código HTTP correspondiente en caso de fallo.
     */
    @DeleteMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Programar la eliminación de la cuenta del usuario autenticado",
            description = "Permite al usuario autenticado solicitar la eliminación de su cuenta. Se requiere la contraseña actual para confirmación. " +
                    "La cuenta se marcará para eliminación y se borrará permanentemente después de un período de gracia. " +
                    "Tras esta operación, la sesión actual del usuario se invalidará. Requiere un token JWT válido.",
            operationId = "deleteMyAccount",
            security = { @SecurityRequirement(name = "bearerAuth") },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "DTO que contiene la contraseña actual del usuario para confirmar la eliminación de la cuenta.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AccountDeleteDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud de eliminación de cuenta procesada. La cuenta ha sido programada para eliminación y la sesión actual invalidada.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(
                                    name = "AccountDeletionScheduled",
                                    summary = "Ejemplo de respuesta exitosa",
                                    value = "{\"message\": \"Tu cuenta ha sido programada para eliminación vuelve a iniciar sesion si quieres mantenerla.\"}"
                            )
                    )),
            @ApiResponse(responseCode = "401", description = "No autorizado. La contraseña actual proporcionada es incorrecta, o el token JWT es inválido/expirado.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))
            ),
            @ApiResponse(responseCode = "404", description = "No encontrado. El usuario autenticado no pudo ser encontrado en la base de datos (caso anómalo).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<Object> deleteMyAccount(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @org.springframework.web.bind.annotation.RequestBody AccountDeleteDTO accountDeleteDTO) {
        try {
            userService.softDeleteUserAccount(currentUser.getEmail(), accountDeleteDTO);
            SecurityContextHolder.clearContext(); // Invalida la sesión actual
            return ResponseEntity.ok().body(java.util.Collections.singletonMap("message", "Tu cuenta ha sido programada para eliminación y ya no es accesible."));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Collections.singletonMap("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Collections.singletonMap("error", e.getMessage()));
        } catch (IllegalStateException e) { // Cambiar estas excepciones.
            return ResponseEntity.badRequest()
                    .body(java.util.Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Collections.singletonMap("error", "Ocurrió un error inesperado al eliminar la cuenta."));
        }
    }

    /**
     * Busca usuarios por su nombre de usuario.
     * La búsqueda es parcial (contiene), ignora mayúsculas/minúsculas y requiere
     * un término de búsqueda de al menos 2 caracteres.
     * El propio usuario que realiza la búsqueda es excluido de los resultados.
     *
     * @param usernameQuery El término de búsqueda para el nombre de usuario.
     * @param currentUser El principal del usuario autenticado que realiza la búsqueda.
     * @return ResponseEntity con una lista de {@link UserSearchResultDTO} con los usuarios encontrados y código HTTP 200 OK.
     * Puede devolver una lista vacía si no hay coincidencias o el término es muy corto.
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar usuarios por nombre de usuario",
            description = "Permite a un usuario autenticado buscar otros usuarios en el sistema por su nombre de usuario. " +
                    "La búsqueda es parcial (contiene) e ignora mayúsculas/minúsculas. " +
                    "El propio usuario que realiza la búsqueda será excluido de los resultados. " +
                    "Se requiere un término de búsqueda de al menos 2 caracteres. Requiere autenticación.",
            operationId = "searchUsersByUsername",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa. Devuelve una lista de usuarios que coinciden con el criterio.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = UserSearchResultDTO.class))
                    )),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta. El parámetro 'username' es obligatorio y debe tener al menos 2 caracteres.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/RequiredErrorResponse") // Asumiendo que existe un esquema para esto
                    )),
            @ApiResponse(responseCode = "401", description = "No autorizado. El token JWT es inválido, ha expirado o no se proporcionó.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/UnauthorizedResponse"))),
            @ApiResponse(responseCode = "404", description = "No encontrado. No se encontraron usuarios con el nombre de usuario proporcionado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<List<UserSearchResultDTO>> searchUsersByUsername(
            @Parameter(name = "username",
                    description = "Término de búsqueda para el nombre de usuario. Debe tener al menos 2 caracteres.",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "jua",
                    schema = @Schema(type = "string", minLength = 2))
            @RequestParam("username") String usernameQuery,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<UserSearchResultDTO> users = userService.searchUsersByUsername(usernameQuery, currentUser.getEmail());
        return ResponseEntity.ok(users);
    }
}