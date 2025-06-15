package mp.tfg.mycheckpoint.service.impl;

import mp.tfg.mycheckpoint.dto.user.*;
import mp.tfg.mycheckpoint.entity.PasswordResetToken;
import mp.tfg.mycheckpoint.entity.User;
import mp.tfg.mycheckpoint.entity.VerificationToken;
import mp.tfg.mycheckpoint.event.OnRegistrationCompleteEvent;
import mp.tfg.mycheckpoint.exception.DuplicateEntryException;
import mp.tfg.mycheckpoint.exception.InvalidTokenException;
import mp.tfg.mycheckpoint.exception.ResourceNotFoundException;
import mp.tfg.mycheckpoint.mapper.UserMapper;
import mp.tfg.mycheckpoint.repository.PasswordResetTokenRepository;
import mp.tfg.mycheckpoint.repository.UserRepository;
import mp.tfg.mycheckpoint.repository.VerificationTokenRepository;
import mp.tfg.mycheckpoint.service.EmailService;
import mp.tfg.mycheckpoint.service.UserService;
import mp.tfg.mycheckpoint.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.*;


/**
 * Implementación del servicio {@link UserService} para la gestión de usuarios.
 * Esta clase proporciona la lógica de negocio para todas las operaciones relacionadas con los usuarios,
 * incluyendo la creación, lectura, actualización y eliminación (CRUD) de perfiles de usuario.
 * También maneja procesos de cuenta como la verificación de correo electrónico, el cambio y
 * restablecimiento de contraseñas, la eliminación programada de cuentas (borrado suave),
 * la búsqueda de usuarios por nombre de usuario y la actualización de la foto de perfil.
 * Todas las operaciones que modifican datos están anotadas como {@link Transactional} para
 * asegurar la atomicidad y consistencia de los datos. Se utilizan mappers para la conversión
 * entre entidades y DTOs, y se interactúa con varios repositorios para la persistencia de datos.
 * Además, se publican eventos para acciones asíncronas como el envío de correos electrónicos
 * y se utiliza un servicio de almacenamiento de archivos para las fotos de perfil.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final FileStorageService fileStorageService;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    /**
     * Define el período de gracia en días antes de que una cuenta programada para eliminación
     * sea borrada permanentemente del sistema.
     */
    private static final int ACCOUNT_DELETION_GRACE_PERIOD_DAYS = 10;


    /**
     * Constructor para {@code UserServiceImpl}.
     * Inyecta todas las dependencias necesarias para la gestión de usuarios y sus datos asociados.
     *
     * @param userRepository Repositorio para el acceso a datos de la entidad {@link User}.
     * @param userMapper Mapper para la conversión entre entidades {@link User} y sus DTOs.
     * @param passwordEncoder Codificador utilizado para hashear y verificar contraseñas de usuario.
     * @param verificationTokenRepository Repositorio para gestionar los tokens de verificación de correo electrónico.
     * @param passwordResetTokenRepository Repositorio para gestionar los tokens de restablecimiento de contraseña.
     * @param emailService Servicio para el envío de correos electrónicos (verificación, reseteo, etc.).
     * @param eventPublisher Publicador de eventos de la aplicación, utilizado para desacoplar acciones como el envío de correos tras el registro.
     * @param fileStorageService Servicio para la gestión del almacenamiento de archivos, como las fotos de perfil.
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           VerificationTokenRepository verificationTokenRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           EmailService emailService,
                           ApplicationEventPublisher eventPublisher,
                           FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.eventPublisher = eventPublisher;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * Antes de la creación, se realizan las siguientes validaciones:
     * Verifica que el correo electrónico proporcionado no esté ya registrado por una cuenta activa.
     * Verifica que el nombre de usuario proporcionado no esté ya en uso por una cuenta activa.
     *
     * La contraseña del usuario se hashea utilizando el {@link PasswordEncoder} configurado.
     * Tras guardar el nuevo usuario, se genera un {@link VerificationToken}, se guarda, y se publica
     * un evento {@link OnRegistrationCompleteEvent} para que un listener envíe el correo de verificación.
     *
     * @param userCreateDTO DTO que contiene los datos para la creación del usuario (nombre de usuario, email y contraseña).
     * @return Un {@link UserDTO} que representa al usuario recién creado, incluyendo su ID público y fecha de registro.
     * @throws DuplicateEntryException Si el correo electrónico o el nombre de usuario ya están registrados y activos.
     */
    @Override
    @Transactional
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.existsByEmailAndFechaEliminacionIsNull(userCreateDTO.getEmail())) {
            throw new DuplicateEntryException("El email '" + userCreateDTO.getEmail() + "' ya está registrado.");
        }
        if (userRepository.existsByNombreUsuarioAndFechaEliminacionIsNull(userCreateDTO.getNombreUsuario())) {
            throw new DuplicateEntryException("El nombre de usuario '" + userCreateDTO.getNombreUsuario() + "' ya está en uso.");
        }
        User userEntity = userMapper.toEntity(userCreateDTO);
        userEntity.setContraseña(passwordEncoder.encode(userCreateDTO.getContraseña()));
        // Los valores por defecto (emailVerified, tema, visibilidadPerfil, publicId, fechas) se gestionan en la entidad o por JPA.
        User savedUser = userRepository.save(userEntity);

        VerificationToken verificationToken = new VerificationToken(savedUser);
        verificationTokenRepository.save(verificationToken);

        // Publica un evento para el envío asíncrono del email de verificación.
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(savedUser, verificationToken.getToken()));
        logger.info("Usuario {} creado y evento de verificación publicado para {}.", savedUser.getNombreUsuario(), savedUser.getEmail());
        return userMapper.toDto(savedUser);
    }

    /**
     * Obtiene un usuario por su ID interno (clave primaria de la base de datos).
     *
     * @param id El ID interno del usuario a buscar.
     * @return Un {@link Optional} que contiene el {@link UserDTO} si se encuentra un usuario con ese ID;
     * de lo contrario, un {@link Optional} vacío.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        // Buscar el usuario por su ID y guardar el resultado en un Optional.
        Optional<User> userOptional = userRepository.findById(id);

        // Comprobar si el Optional contiene un valor.
        if (userOptional.isPresent()) {
            // Si existe, obtener la entidad User.
            User user = userOptional.get();
            // Convertir la entidad a DTO.
            UserDTO userDTO = userMapper.toDto(user);
            // Devolver un Optional que contiene el DTO.
            return Optional.of(userDTO);
        } else {
            // Si no existe, devolver un Optional vacío.
            return Optional.empty();
        }
    }

    /**
     * Obtiene un usuario por su ID público (UUID).
     *
     * @param publicId El ID público (UUID) del usuario a buscar.
     * @return Un {@link Optional} que contiene el {@link UserDTO} si se encuentra un usuario con ese ID público;
     * de lo contrario, un {@link Optional} vacío.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByPublicId(UUID publicId) {
        Optional<User> userOptional = userRepository.findByPublicId(publicId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDTO userDTO = userMapper.toDto(user);
            return Optional.of(userDTO);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Obtiene un usuario por su ID público (UUID) y lo mapea a un {@link UserSearchResultDTO}.

     * @param publicId El ID público (UUID) del usuario que se desea obtener.
     * @return Un {@link Optional} que contiene el {@link UserSearchResultDTO} si el usuario
     * fue encontrado y mapeado exitosamente; de lo contrario, un {@link Optional} vacío.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserSearchResultDTO> getUserByPublicIdAsSearchResult(UUID publicId) {
        Optional<User> userOptional = userRepository.findByPublicId(publicId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserSearchResultDTO searchResultDTO = userMapper.toSearchResultDto(user);
            return Optional.of(searchResultDTO);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Obtiene un usuario por su dirección de correo electrónico.
     *
     * @param email El correo electrónico del usuario a buscar.
     * @return Un {@link Optional} que contiene el {@link UserDTO} si se encuentra un usuario con ese email;
     * de lo contrario, un {@link Optional} vacío.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDTO userDTO = userMapper.toDto(user);
            return Optional.of(userDTO);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Actualiza el perfil de un usuario existente, identificado por su correo electrónico.
     * Solo los campos presentes y no nulos en el {@link UserProfileUpdateDTO} se utilizarán para
     * la actualización. Si se intenta cambiar el nombre de usuario a uno que ya está en uso
     * por otro usuario diferente, se lanzará una {@link DuplicateEntryException}.
     *
     * @param userEmail El correo electrónico del usuario cuyo perfil se va a actualizar.
     * @param profileUpdateDTO DTO que contiene los datos del perfil a actualizar (nombre de usuario, tema, foto, etc.).
     * @return Un {@link UserDTO} que representa el perfil del usuario después de la actualización.
     * @throws ResourceNotFoundException Si no se encuentra ningún usuario con el {@code userEmail} proporcionado.
     * @throws DuplicateEntryException Si el nuevo nombre de usuario especificado en {@code profileUpdateDTO}
     * ya está en uso por otro usuario.
     */
    @Override
    @Transactional
    public UserDTO updateUserProfile(String userEmail, UserProfileUpdateDTO profileUpdateDTO) {
        User userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + userEmail + " para actualizar perfil."));

        if (StringUtils.hasText(profileUpdateDTO.getNombreUsuario()) &&
                !profileUpdateDTO.getNombreUsuario().equals(userEntity.getNombreUsuario())) {
            Optional<User> existingUserWithNewUsername = userRepository.findByNombreUsuario(profileUpdateDTO.getNombreUsuario());
            if (existingUserWithNewUsername.isPresent() && !existingUserWithNewUsername.get().getEmail().equals(userEmail)) {
                throw new DuplicateEntryException("El nuevo nombre de usuario '" + profileUpdateDTO.getNombreUsuario() + "' ya está en uso.");
            }
        }
        userMapper.updateProfileFromDto(profileUpdateDTO, userEntity); // MapStruct actualiza solo campos no nulos del DTO
        User updatedUser = userRepository.save(userEntity);
        logger.info("Perfil del usuario {} actualizado.", userEmail);
        return userMapper.toDto(updatedUser);
    }

    /**
     * Confirma la dirección de correo electrónico de un usuario utilizando un token de verificación.
     * El método busca el token proporcionado. Si el token es válido (existe, no está usado y no ha expirado),
     * se marca el correo electrónico del usuario asociado como verificado ({@code emailVerified = true}) y
     * el token se marca como usado para prevenir su reutilización.
     *
     * @param tokenString El token de verificación (en formato String) enviado al usuario.
     * @return Un mensaje de cadena indicando el resultado del proceso de confirmación
     * (ej. "¡Tu correo electrónico ha sido verificado exitosamente!").
     * @throws InvalidTokenException Si el token es inválido, no se encuentra, ya ha sido utilizado,
     * o si el correo del usuario ya estaba verificado.
     * @throws ResourceNotFoundException Si el usuario asociado al token no puede ser encontrado (caso anómalo).
     */
    @Override
    @Transactional
    public String confirmEmailVerification(String tokenString) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new InvalidTokenException("Token de verificación inválido o no encontrado."));

        if (verificationToken.isUsed()) {
            throw new InvalidTokenException("Este enlace de verificación ya ha sido utilizado.");
        }
        if (verificationToken.isExpired()) {
            verificationTokenRepository.delete(verificationToken);
            throw new InvalidTokenException("El enlace de verificación ha expirado.");
        }

        User user = verificationToken.getUser();
        if (user == null) {
            logger.error("No se encontró usuario para el token de verificación ID: {}", verificationToken.getId());
            throw new ResourceNotFoundException("Usuario asociado al token no encontrado, ID de token: " + verificationToken.getId());
        }
        if (user.isEmailVerified()) {
            // Si el email ya está verificado, simplemente se marca el token como usado para invalidarlo.
            verificationToken.setUsed(true);
            verificationTokenRepository.save(verificationToken);
            throw new InvalidTokenException("Tu correo electrónico ya ha sido verificado.");
        }

        user.setEmailVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);
        logger.info("Correo electrónico verificado para el usuario {}.", user.getEmail());
        return "¡Tu correo electrónico ha sido verificado exitosamente! Ahora puedes iniciar sesión.";
    }

    /**
     * Cambia la contraseña de un usuario.
     * Primero, se verifica si la {@code contraseñaActual} proporcionada coincide con la contraseña
     * almacenada del usuario. Si la verificación es exitosa, se comprueba que la
     * {@code nuevaContraseña} no sea igual a la actual. Finalmente, la nueva contraseña se hashea
     * y se actualiza en la base de datos.
     *
     * @param userEmail El correo electrónico del usuario cuya contraseña se va a cambiar.
     * @param passwordChangeDTO DTO que contiene la contraseña actual del usuario y la nueva contraseña deseada.
     * @throws ResourceNotFoundException Si no se encuentra ningún usuario con el {@code userEmail} proporcionado.
     * @throws BadCredentialsException Si la {@code contraseñaActual} proporcionada no coincide con la del usuario.
     * @throws IllegalArgumentException Si la {@code nuevaContraseña} es igual a la contraseña actual.
     */
    @Override
    @Transactional
    public void changePassword(String userEmail, PasswordChangeDTO passwordChangeDTO) {
        User userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + userEmail));

        if (!passwordEncoder.matches(passwordChangeDTO.getContraseñaActual(), userEntity.getContraseña())) {
            throw new BadCredentialsException("La contraseña actual proporcionada es incorrecta.");
        }
        if (passwordEncoder.matches(passwordChangeDTO.getNuevaContraseña(), userEntity.getContraseña())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la contraseña actual.");
        }

        userEntity.setContraseña(passwordEncoder.encode(passwordChangeDTO.getNuevaContraseña()));
        userRepository.save(userEntity);
        logger.info("Contraseña actualizada para el usuario {}.", userEmail);
    }

    /**
     * Inicia el proceso de "olvido de contraseña" para un usuario.
     * Si se encuentra un usuario con el correo electrónico proporcionado en {@link ForgotPasswordDTO}:
     * Se invalidan (marcan como usados) los tokens de restablecimiento de contraseña anteriores,
     * activos y no expirados para ese usuario.
     * Se genera un nuevo {@link PasswordResetToken}.
     * Se guarda el nuevo token en la base de datos.
     * Se envía un correo electrónico al usuario con el nuevo token e instrucciones para restablecer su contraseña.
     * Por razones de seguridad, si el correo electrónico no se encuentra, se lanza una {@link ResourceNotFoundException}
     * que el controlador debe manejar para dar una respuesta genérica al cliente, evitando así revelar
     * si una dirección de correo electrónico está registrada o no en el sistema.
     *
     * @param forgotPasswordDTO DTO que contiene el correo electrónico del usuario que ha olvidado su contraseña.
     * @throws ResourceNotFoundException Si no se encuentra ningún usuario con el correo electrónico proporcionado.
     * El llamador (controlador) es responsable de manejar esta excepción adecuadamente.
     */
    @Override
    @Transactional
    public void processForgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        //Buscar al usuario por su email.
        Optional<User> userOptional = userRepository.findByEmail(forgotPasswordDTO.getEmail());

        //Comprobar si el usuario no fue encontrado para lanzar la excepción.
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado con email: " + forgotPasswordDTO.getEmail());
        }
        //Si se encontró, obtener la entidad del usuario.
        User user = userOptional.get();
        //Buscar si existe un token de reseteo anterior y activo.
        Optional<PasswordResetToken> existingTokenOptional = passwordResetTokenRepository
                .findByUserAndUsedFalseAndExpiryDateAfter(user, OffsetDateTime.now());
        //Si se encuentra un token activo, invalidarlo.
        if (existingTokenOptional.isPresent()) {
            PasswordResetToken existingToken = existingTokenOptional.get();
            logger.info("Invalidando token de reseteo de contraseña anterior (ID: {}) para el usuario {}.", existingToken.getId(), user.getEmail());
            existingToken.setUsed(true);
            passwordResetTokenRepository.save(existingToken);
        }
        //Crear y guardar el nuevo token de restablecimiento.
        PasswordResetToken passwordResetToken = new PasswordResetToken(user);
        passwordResetTokenRepository.save(passwordResetToken);
        //Enviar el correo electrónico con el nuevo token.
        emailService.sendPasswordResetEmail(user, passwordResetToken.getToken());
    }

    /**
     * Procesa el restablecimiento de la contraseña de un usuario utilizando un token de restablecimiento.
     * Se realizan las siguientes validaciones sobre el token:
     * Debe existir en la base de datos.
     * No debe haber sido utilizado previamente.
     * No debe haber expirado.
     * Si el token es válido, se actualiza la contraseña del usuario asociado (previamente hasheada).
     * Adicionalmente, se verifica que la nueva contraseña no sea igual a la contraseña actual del usuario.
     * El token de restablecimiento se marca como usado después del proceso.
     *
     * @param resetPasswordDTO DTO que contiene el token de restablecimiento y la nueva contraseña deseada.
     * @return Un mensaje de cadena confirmando el éxito del restablecimiento de la contraseña.
     * @throws ResourceNotFoundException Si el token no se encuentra o el usuario asociado al token no existe.
     * @throws InvalidTokenException Si el token ya ha sido utilizado.
     * @throws IllegalStateException Si el token ha expirado (en este caso, también se elimina el token expirado).
     * @throws IllegalArgumentException Si la nueva contraseña es idéntica a la contraseña actual del usuario.
     */
    @Override
    @Transactional
    public String processResetPassword(ResetPasswordDTO resetPasswordDTO) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(resetPasswordDTO.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Token de restablecimiento de contraseña inválido o no encontrado."));

        if (passwordResetToken.isUsed()) {
            throw new InvalidTokenException("Este token de restablecimiento ya ha sido utilizado.");
        }
        if (passwordResetToken.isExpired()) {
            passwordResetTokenRepository.delete(passwordResetToken); // Limpiar token expirado
            throw new IllegalStateException("El token de restablecimiento ha expirado. Por favor, solicita uno nuevo.");
        }

        User user = passwordResetToken.getUser();
        if (user == null) {
            logger.error("No se encontró usuario para el token de reseteo ID: {}", passwordResetToken.getId());
            throw new ResourceNotFoundException("Usuario asociado al token de reseteo no encontrado, ID de token: " + passwordResetToken.getId());
        }

        if (passwordEncoder.matches(resetPasswordDTO.getNuevaContraseña(), user.getContraseña())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la contraseña actual.");
        }

        user.setContraseña(passwordEncoder.encode(resetPasswordDTO.getNuevaContraseña()));
        userRepository.save(user);

        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
        logger.info("Contraseña restablecida para el usuario {} mediante token.", user.getEmail());
        return "Tu contraseña ha sido restablecida exitosamente. Ahora puedes iniciar sesión con tu nueva contraseña.";
    }

    /**
     * Programa la cuenta de un usuario para su eliminación (soft delete).
     * Se requiere la contraseña actual del usuario para confirmar la acción. Si la contraseña es válida,
     * se establece una fecha de eliminación futura en el perfil del usuario (definida por
     * {@code ACCOUNT_DELETION_GRACE_PERIOD_DAYS}). Durante este período de gracia, el usuario puede
     * cancelar la eliminación simplemente iniciando sesión.
     * Los tokens de verificación de correo y de restablecimiento de contraseña asociados al usuario
     * también se eliminan para prevenir su uso.
     *
     * @param userEmail El correo electrónico del usuario cuya cuenta se programará para eliminación.
     * @param accountDeleteDTO DTO que contiene la contraseña actual del usuario para verificación.
     * @throws ResourceNotFoundException Si no se encuentra ningún usuario con el {@code userEmail} proporcionado.
     * @throws BadCredentialsException Si la {@code contraseñaActual} proporcionada es incorrecta.
     * @throws IllegalStateException Si la cuenta del usuario ya tiene una eliminación programada activa.
     */
    @Override
    @Transactional
    public void softDeleteUserAccount(String userEmail, AccountDeleteDTO accountDeleteDTO) {
        logger.info("Solicitando programación de eliminación para la cuenta del usuario: {}", userEmail);
        User userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado con email: {} al intentar programar eliminación de cuenta.", userEmail);
                    return new ResourceNotFoundException("Usuario no encontrado con email: " + userEmail);
                });

        if (userEntity.getFechaEliminacion() != null && userEntity.getFechaEliminacion().isAfter(OffsetDateTime.now())) {
            logger.warn("La cuenta para el usuario: {} ya tiene una eliminación programada para {}.", userEmail, userEntity.getFechaEliminacion());
            throw new IllegalStateException("Tu cuenta ya está programada para eliminación el " +
                    userEntity.getFechaEliminacion().toLocalDate() +
                    ". Si deseas cancelar esta acción, simplemente inicia sesión.");
        }

        if (!passwordEncoder.matches(accountDeleteDTO.getContraseñaActual(), userEntity.getContraseña())) {
            logger.warn("Contraseña actual incorrecta al intentar programar eliminación de cuenta para el usuario: {}", userEmail);
            throw new BadCredentialsException("La contraseña actual proporcionada es incorrecta.");
        }

        OffsetDateTime futureDeletionDate = OffsetDateTime.now().plusDays(ACCOUNT_DELETION_GRACE_PERIOD_DAYS);
        userEntity.setFechaEliminacion(futureDeletionDate);
        logger.info("Eliminación de cuenta programada para el usuario: {} en la fecha: {}", userEmail, futureDeletionDate);

        verificationTokenRepository.findByUser(userEntity).ifPresent(verificationTokenRepository::delete);
        passwordResetTokenRepository.findByUser(userEntity).ifPresent(passwordResetTokenRepository::delete);

        userRepository.save(userEntity);
        logger.info("Solicitud de eliminación de cuenta procesada y programada exitosamente para el usuario: {}", userEmail);
    }

    /**
     * Busca usuarios por una subcadena de su nombre de usuario, ignorando mayúsculas y minúsculas.
     * El término de búsqueda ({@code usernameQuery}) debe tener al menos 2 caracteres.
     * El usuario que realiza la búsqueda (identificado por {@code currentUserEmail}) es excluido de los resultados.
     * Solo se devuelven usuarios cuyas cuentas no estén marcadas para eliminación.
     *
     * @param usernameQuery El término de búsqueda para el nombre de usuario.
     * @param currentUserEmail El correo electrónico del usuario que realiza la búsqueda (para autoexclusión).
     * @return Una lista de {@link UserSearchResultDTO} con los usuarios encontrados que coinciden con el criterio.
     * Retorna una lista vacía si el término de búsqueda es demasiado corto.
     * @throws ResourceNotFoundException Si el {@code currentUserEmail} no corresponde a un usuario existente,
     * o si no se encuentran usuarios que coincidan con la consulta (y el término es válido).
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserSearchResultDTO> searchUsersByUsername(String usernameQuery, String currentUserEmail) {
        if (!StringUtils.hasText(usernameQuery) || usernameQuery.trim().length() < 2) {
            logger.debug("Término de búsqueda '{}' demasiado corto para la búsqueda de usuarios. Se requiere un mínimo de 2 caracteres.", usernameQuery);
            return Collections.emptyList();
        }

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario actual (solicitante con email: " + currentUserEmail + ") no encontrado. Búsqueda no realizada."));

        List<User> foundUsers = userRepository.searchByNombreUsuarioContainingIgnoreCaseAndNotSelf(
                usernameQuery.trim(),
                currentUser.getId()
        );

        if (foundUsers.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron usuarios con el nombre: '" + usernameQuery.trim() + "'");
        }
        logger.info("Búsqueda de usuarios por '{}' realizada por el usuario {} encontró {} resultados.", usernameQuery, currentUserEmail, foundUsers.size());

        List<UserSearchResultDTO> resultDTOs = new ArrayList<>();
        for (User foundUser : foundUsers) {
            resultDTOs.add(userMapper.toSearchResultDto(foundUser));
        }
        return resultDTOs;
    }

    /**
     * Actualiza la foto de perfil de un usuario.
     * Este método asume que el archivo de imagen ya ha sido guardado por el {@link FileStorageService}
     * y que {@code profilePictureFileName} es el nombre del archivo resultante de esa operación.
     * Se actualiza la referencia al nombre del archivo en la entidad del usuario.
     * Si el usuario tenía una foto de perfil anterior diferente a la nueva, el archivo antiguo
     * se elimina del sistema de almacenamiento a través del {@code FileStorageService}.
     *
     * @param userEmail El correo electrónico del usuario cuya foto de perfil se va a actualizar.
     * @param profilePictureFileName El nombre del nuevo archivo de imagen de perfil (ej. "uuid_del_usuario.jpg").
     * @return Un {@link UserDTO} que representa al usuario con la foto de perfil actualizada.
     * @throws ResourceNotFoundException Si no se encuentra ningún usuario con el {@code userEmail} proporcionado.
     */
    @Override
    @Transactional
    public UserDTO updateUserProfilePicture(String userEmail, String profilePictureFileName) {
        User userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + userEmail));

        String oldProfilePicture = userEntity.getFotoPerfil();

        if (oldProfilePicture != null && !oldProfilePicture.isBlank() && !oldProfilePicture.equals(profilePictureFileName)) {
            // Se delega la lógica de si el archivo existe y debe ser borrado al FileStorageService
            fileStorageService.deleteProfilePicture(oldProfilePicture);
            logger.info("Foto de perfil antigua '{}' marcada para eliminación (si existía) para el usuario {}.", oldProfilePicture, userEmail);
        }

        userEntity.setFotoPerfil(profilePictureFileName);
        User updatedUser = userRepository.save(userEntity);
        logger.info("Foto de perfil actualizada en la base de datos para el usuario {} a: {}", userEmail, profilePictureFileName);
        return userMapper.toDto(updatedUser);
    }
}