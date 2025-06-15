package mp.tfg.mycheckpoint.repository;

import mp.tfg.mycheckpoint.entity.User;
import mp.tfg.mycheckpoint.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link VerificationToken}.
 * Gestiona la persistencia de los tokens utilizados para la verificación de correo electrónico.
 */
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /**
     * Busca un token de verificación por su valor de token.
     *
     * @param token El valor del token a buscar.
     * @return Un {@link Optional} que contiene el {@link VerificationToken} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<VerificationToken> findByToken(String token);

    /**
     * Busca un token de verificación asociado a un usuario específico.
     * Útil para, por ejemplo, reenviar un token de verificación o verificar si ya existe uno.
     *
     * @param user El {@link User} para el cual buscar el token.
     * @return Un {@link Optional} que contiene el {@link VerificationToken} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<VerificationToken> findByUser(User user);

    /**
     * Elimina todos los tokens de verificación que han expirado antes de la fecha y hora especificadas.
     * Útil para tareas de limpieza programadas.
     *
     * @param now La fecha y hora actual; los tokens que expiraron antes de este momento serán eliminados.
     */
    void deleteAllByExpiryDateBefore(OffsetDateTime now);

    /**
     * Elimina los tokens de verificación de un usuario específico que coinciden con el estado 'used'.
     * Por ejemplo, para eliminar tokens ya utilizados.
     *
     * @param user El {@link User} cuyos tokens se eliminarán.
     * @param used {@code true} para eliminar tokens usados, {@code false} para eliminar tokens no usados.
     */
    void deleteByUserAndUsed(User user, boolean used);
}