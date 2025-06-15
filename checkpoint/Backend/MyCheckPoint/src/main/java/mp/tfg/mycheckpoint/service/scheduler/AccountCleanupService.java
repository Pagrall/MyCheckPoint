package mp.tfg.mycheckpoint.service.scheduler;


import mp.tfg.mycheckpoint.entity.GameList;
import mp.tfg.mycheckpoint.entity.TierList;
import mp.tfg.mycheckpoint.entity.User;
import mp.tfg.mycheckpoint.entity.UserGame;
import mp.tfg.mycheckpoint.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;


/**
 * Servicio programado para realizar la limpieza de cuentas de usuario.
 * Este servicio se encarga de eliminar permanentemente las cuentas de usuario
 * que han sido marcadas para borrado suave y cuyo período de gracia ha expirado.
 * La limpieza incluye la eliminación de todos los datos asociados al usuario.
 */
@Service
public class AccountCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(AccountCleanupService.class);
    private final UserRepository userRepository;
    private final UserGameRepository userGameRepository;
    private final GameListRepository gameListRepository;
    private final TierListRepository tierListRepository;
    private final FriendshipRepository friendshipRepository;
    private final VerificationTokenRepository verificationTokenRepository; // Para tokens de verificación
    private final PasswordResetTokenRepository passwordResetTokenRepository; // Para tokens de reseteo

    /**
     * Constructor para {@code AccountCleanupService}.
     * Inyecta los repositorios necesarios para acceder y eliminar los datos
     * de los usuarios y sus entidades relacionadas.
     *
     * @param userRepository Repositorio para las entidades {@link User}.
     * @param userGameRepository Repositorio para las entidades {@link UserGame}.
     * @param gameListRepository Repositorio para las entidades {@link GameList}.
     * @param tierListRepository Repositorio para las entidades {@link TierList}.
     * @param friendshipRepository Repositorio para las entidades {@link mp.tfg.mycheckpoint.entity.Friendship}.
     * @param verificationTokenRepository Repositorio para los {@link mp.tfg.mycheckpoint.entity.VerificationToken}.
     * @param passwordResetTokenRepository Repositorio para los {@link mp.tfg.mycheckpoint.entity.PasswordResetToken}.
     */
    @Autowired
    public AccountCleanupService(UserRepository userRepository,
                                 UserGameRepository userGameRepository,
                                 GameListRepository gameListRepository,
                                 TierListRepository tierListRepository,
                                 FriendshipRepository friendshipRepository,
                                 VerificationTokenRepository verificationTokenRepository,
                                 PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.userGameRepository = userGameRepository;
        this.gameListRepository = gameListRepository;
        this.tierListRepository = tierListRepository;
        this.friendshipRepository = friendshipRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }


    /**
     * Tarea programada que se ejecuta periódicamente para eliminar definitivamente
     * las cuentas de usuario cuya fecha de eliminación programada (soft delete)
     * ya ha pasado. La ejecución está definida por una expresión Cron.
     * Esta operación es transaccional para asegurar la atomicidad de los borrados.
     * El proceso de eliminación incluye:
     * Entradas de {@link UserGame} (biblioteca de juegos del usuario).
     * Entradas de {@link GameList} (listas de juegos creadas por el usuario).
     * Entradas de {@link mp.tfg.mycheckpoint.entity.Friendship} (amistades y solicitudes).
     * Tokens de verificación y de reseteo de contraseña asociados al usuario.
     * Finalmente, la entidad {@link User} misma.
     * Se registran logs detallados del proceso y de cualquier error que pueda ocurrir.
     */
    //0 * * * * ? / 0 0 2 * * ?
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional // Importante para que la operación de borrado sea atómica
    public void performScheduledAccountDeletions() {
        OffsetDateTime now = OffsetDateTime.now();
        logger.info("Iniciando tarea de limpieza de cuentas programadas para eliminación antes de o igual a: {}", now);

        List<User> accountsToDelete = userRepository.findUsersScheduledForDeletion(now);

        if (accountsToDelete.isEmpty()) {
            logger.info("No se encontraron cuentas para eliminación definitiva en esta ejecución.");
            return;
        }

        logger.info("Se encontraron {} cuentas para eliminación definitiva.", accountsToDelete.size());
        for (User user : accountsToDelete) {
            logger.warn("Eliminando definitivamente la cuenta del usuario con email: {} (ID: {}, PublicID: {}) programada para el {}",
                    user.getEmail(), user.getId(), user.getPublicId(), user.getFechaEliminacion());
            try {

                List<TierList> tierLists = tierListRepository.findAllByOwnerAndTypeWithSections(user);
                if (!tierLists.isEmpty()) {
                    tierListRepository.deleteAll(tierLists);
                    logger.info("Eliminadas {} listas de TierList para el usuario {}.", tierLists.size(), user.getEmail());
                }

                List<UserGame> userGames = userGameRepository.findByUser(user);
                if (!userGames.isEmpty()) {
                    userGameRepository.deleteAll(userGames);
                    logger.info("Eliminadas {} entradas de UserGame para el usuario {}.", userGames.size(), user.getEmail());
                }

                List<GameList> gameLists = gameListRepository.findByOwnerOrderByUpdatedAtDesc(user);
                if (!gameLists.isEmpty()) {
                    gameListRepository.deleteAll(gameLists);
                    logger.info("Eliminadas {} listas de GameList para el usuario {}.", gameLists.size(), user.getEmail());
                }
                friendshipRepository.deleteAllInvolvingUser(user);
                logger.info("Eliminadas todas las amistades que involucran al usuario {}.", user.getEmail());

                verificationTokenRepository.findByUser(user).ifPresent(token -> {
                    verificationTokenRepository.delete(token);
                    logger.info("Eliminado token de verificación para el usuario {}.", user.getEmail());
                });
                passwordResetTokenRepository.findByUser(user).ifPresent(token -> {
                    passwordResetTokenRepository.delete(token);
                    logger.info("Eliminado token de reseteo de contraseña para el usuario {}.", user.getEmail());
                });

                userRepository.delete(user);
                logger.info("Cuenta del usuario {} eliminada exitosamente de la base de datos.", user.getEmail());
            } catch (Exception e) {
                logger.error("Error al eliminar definitivamente la cuenta del usuario {}: {}. Se procederá con otros si los hay.", user.getEmail(), e.getMessage(), e);
            }
        }
        logger.info("Tarea de limpieza de cuentas completada.");
    }
}