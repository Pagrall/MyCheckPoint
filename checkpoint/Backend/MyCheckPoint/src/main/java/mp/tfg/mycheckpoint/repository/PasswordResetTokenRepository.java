package mp.tfg.mycheckpoint.repository;

import mp.tfg.mycheckpoint.entity.PasswordResetToken;
import mp.tfg.mycheckpoint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link PasswordResetToken}.
 * Gestiona la persistencia de los tokens utilizados para el proceso de restablecimiento de contraseña.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Busca un token de restablecimiento de contraseña por su valor de token.
     *
     * @param token El valor del token a buscar.
     * @return Un {@link Optional} que contiene el {@link PasswordResetToken} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Busca un token de restablecimiento de contraseña activo (no usado y no expirado) para un usuario específico.
     *
     * @param user El {@link User} para el cual buscar el token.
     * @param now La fecha y hora actual, para comparar con la fecha de expiración del token.
     * @return Un {@link Optional} que contiene el {@link PasswordResetToken} activo si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<PasswordResetToken> findByUserAndUsedFalseAndExpiryDateAfter(User user, OffsetDateTime now);

    /**
     * Elimina todos los tokens de restablecimiento de contraseña asociados a un usuario específico.
     * Útil para invalidar tokens antiguos cuando se genera uno nuevo o el usuario cambia su contraseña.
     *
     * @param user El {@link User} cuyos tokens serán eliminados.
     */
    void deleteByUser(User user);

    /**
     * Busca un token de restablecimiento de contraseña asociado a un usuario,
     * independientemente de si está usado o ha expirado.
     * Útil para encontrar y eliminar tokens antiguos antes de crear uno nuevo.
     *
     * @param userEntity El {@link User} para el cual buscar el token.
     * @return Un {@link Optional} que contiene el {@link PasswordResetToken} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<PasswordResetToken> findByUser(User userEntity);
}
