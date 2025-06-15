package mp.tfg.mycheckpoint.service;

import mp.tfg.mycheckpoint.dto.user.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interfaz para el servicio de gestión de usuarios.
 * Define operaciones para crear, obtener, actualizar y eliminar usuarios,
 * así como para manejar procesos relacionados con la cuenta como verificación de email,
 * cambio de contraseña, y restablecimiento de contraseña. También incluye
 * la búsqueda de usuarios y la actualización de la foto de perfil.
 */
public interface UserService {

    /**
     * Crea un nuevo usuario en el sistema.
     * Envía un correo de verificación tras el registro exitoso.
     *
     * @param userCreateDTO DTO con los datos para la creación del usuario.
     * @return Un {@link UserDTO} representando al usuario recién creado.
     * @throws mp.tfg.mycheckpoint.exception.DuplicateEntryException Si el email o nombre de usuario ya existen.
     */
    UserDTO createUser(UserCreateDTO userCreateDTO);

    /**
     * Obtiene un usuario por su ID interno.
     *
     * @param id El ID interno del usuario.
     * @return Un {@link Optional} que contiene el {@link UserDTO} si se encuentra, o vacío si no.
     */
    Optional<UserDTO> getUserById(Long id);

    /**
     * Obtiene un usuario por su ID público (UUID).
     *
     * @param publicId El ID público del usuario.
     * @return Un {@link Optional} que contiene el {@link UserDTO} si se encuentra, o vacío si no.
     */
    Optional<UserDTO> getUserByPublicId(UUID publicId);

    /**
     * Obtiene un usuario por su ID público (UUID) como un DTO de resultado de búsqueda.
     *
     * @param publicId El ID público del usuario.
     * @return Un {@link Optional} que contiene el {@link UserSearchResultDTO} si se encuentra, o vacío si no.
     */
    Optional<UserSearchResultDTO> getUserByPublicIdAsSearchResult(UUID publicId);

    /**
     * Obtiene un usuario por su dirección de correo electrónico.
     *
     * @param email El email del usuario.
     * @return Un {@link Optional} que contiene el {@link UserDTO} si se encuentra, o vacío si no.
     */
    Optional<UserDTO> getUserByEmail(String email);

    /**
     * Actualiza el perfil del usuario autenticado actualmente.
     * Solo los campos proporcionados en {@code profileUpdateDTO} serán actualizados.
     *
     * @param userEmail El email del usuario cuyo perfil se va a actualizar.
     * @param profileUpdateDTO DTO con los datos del perfil a actualizar.
     * @return Un {@link UserDTO} representando el perfil de usuario actualizado.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario no se encuentra.
     * @throws mp.tfg.mycheckpoint.exception.DuplicateEntryException Si el nuevo nombre de usuario elegido ya está en uso.
     */
    UserDTO updateUserProfile(String userEmail, UserProfileUpdateDTO profileUpdateDTO);

    /**
     * Confirma la dirección de correo electrónico de un usuario utilizando un token de verificación.
     * Marca el email del usuario como verificado si el token es válido y no ha expirado.
     *
     * @param token El token de verificación recibido por el usuario.
     * @return Un mensaje indicando el resultado del proceso de confirmación.
     * @throws mp.tfg.mycheckpoint.exception.InvalidTokenException Si el token es inválido, ha expirado o ya fue usado.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario asociado al token no existe.
     */
    String confirmEmailVerification(String token);

    /**
     * Cambia la contraseña del usuario especificado.
     * Requiere la contraseña actual para verificación.
     *
     * @param userEmail El email del usuario que desea cambiar su contraseña.
     * @param passwordChangeDTO DTO que contiene la contraseña actual y la nueva contraseña.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario no se encuentra.
     * @throws org.springframework.security.authentication.BadCredentialsException Si la contraseña actual es incorrecta.
     * @throws IllegalArgumentException Si la nueva contraseña es igual a la actual o no cumple otros criterios.
     */
    void changePassword(String userEmail, PasswordChangeDTO passwordChangeDTO);

    /**
     * Inicia el proceso de olvido de contraseña para el usuario con el email proporcionado.
     * Si el email existe, genera un token de restablecimiento y envía un correo al usuario.
     * Por razones de seguridad, no revela si el email fue encontrado o no.
     *
     * @param forgotPasswordDTO DTO que contiene el email del usuario.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario con el email proporcionado no se encuentra.
     * (Nota: el controlador puede optar por no propagar esta excepción para ofuscar la existencia del email).
     */
    void processForgotPassword(ForgotPasswordDTO forgotPasswordDTO);

    /**
     * Restablece la contraseña de un usuario utilizando un token de restablecimiento válido.
     *
     * @param resetPasswordDTO DTO que contiene el token de restablecimiento y la nueva contraseña.
     * @return Un mensaje indicando el resultado del proceso de restablecimiento.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el token no se encuentra o el usuario asociado no existe.
     * @throws mp.tfg.mycheckpoint.exception.InvalidTokenException Si el token es inválido, ha expirado o ya fue usado.
     * @throws IllegalArgumentException Si la nueva contraseña es igual a la actual.
     */
    String processResetPassword(ResetPasswordDTO resetPasswordDTO);

    /**
     * Marca la cuenta del usuario para eliminación programada (borrado suave).
     * La cuenta se eliminará permanentemente después de un período de gracia.
     * Invalida tokens de verificación y reseteo asociados.
     *
     * @param userEmail El email del usuario cuya cuenta se va a programar para eliminación.
     * @param accountDeleteDTO DTO que contiene la contraseña actual para confirmación.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario no se encuentra.
     * @throws org.springframework.security.authentication.BadCredentialsException Si la contraseña actual es incorrecta.
     * @throws IllegalStateException Si la cuenta ya está programada para eliminación.
     */
    void softDeleteUserAccount(String userEmail, AccountDeleteDTO accountDeleteDTO);

    /**
     * Busca usuarios por su nombre de usuario. La búsqueda es parcial e ignora mayúsculas/minúsculas.
     * Excluye al usuario que realiza la búsqueda de los resultados.
     * Requiere que el término de búsqueda tenga al menos 2 caracteres.
     *
     * @param usernameQuery El término de búsqueda para el nombre de usuario.
     * @param currentUserEmail El email del usuario que realiza la búsqueda (para excluirlo).
     * @return Una lista de {@link UserSearchResultDTO} con los usuarios encontrados.
     * Devuelve una lista vacía si el término de búsqueda es muy corto.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si no se encuentran usuarios o el usuario actual no existe.
     */
    List<UserSearchResultDTO> searchUsersByUsername(String usernameQuery, String currentUserEmail);

    /**
     * Actualiza la foto de perfil de un usuario.
     * Guarda el nuevo archivo de imagen y actualiza la referencia en la entidad del usuario.
     * Si existe una foto de perfil anterior, se elimina del almacenamiento.
     *
     * @param userEmail El email del usuario cuya foto de perfil se va a actualizar.
     * @param profilePictureFileName El nombre del archivo de la nueva foto de perfil (generado por el servicio de almacenamiento).
     * @return Un {@link UserDTO} representando al usuario con la foto de perfil actualizada.
     * @throws mp.tfg.mycheckpoint.exception.ResourceNotFoundException Si el usuario no se encuentra.
     * @throws mp.tfg.mycheckpoint.exception.FileStorageException Si ocurre un error al manejar el archivo.
     */
    UserDTO updateUserProfilePicture(String userEmail, String profilePictureFileName);
}