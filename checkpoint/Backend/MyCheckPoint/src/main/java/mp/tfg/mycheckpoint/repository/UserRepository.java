package mp.tfg.mycheckpoint.repository;

import mp.tfg.mycheckpoint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de Spring Data JPA para la entidad {@link User}.
 * Proporciona métodos CRUD básicos y consultas personalizadas para acceder a los datos de los usuarios.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     *
     * @param email La dirección de correo electrónico a buscar.
     * @return Un {@link Optional} que contiene el {@link User} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param nombreUsuario El nombre de usuario a buscar.
     * @return Un {@link Optional} que contiene el {@link User} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<User> findByNombreUsuario(String nombreUsuario);

    /**
     * Busca un usuario por su ID público (UUID).
     *
     * @param publicId El ID público del usuario a buscar.
     * @return Un {@link Optional} que contiene el {@link User} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<User> findByPublicId(UUID publicId);

    /**
     * Comprueba si existe un usuario con la dirección de correo electrónico especificada
     * y que no esté marcado para eliminación ({@code fechaEliminacion} es nulo).
     *
     * @param email La dirección de correo electrónico a comprobar.
     * @return {@code true} si existe un usuario activo con ese email, {@code false} en caso contrario.
     */
    boolean existsByEmailAndFechaEliminacionIsNull(String email);

    /**
     * Comprueba si existe un usuario con el nombre de usuario especificado
     * y que no esté marcado para eliminación ({@code fechaEliminacion} es nulo).
     *
     * @param nombreUsuario El nombre de usuario a comprobar.
     * @return {@code true} si existe un usuario activo con ese nombre de usuario, {@code false} en caso contrario.
     */
    boolean existsByNombreUsuarioAndFechaEliminacionIsNull(String nombreUsuario);

    /**
     * Encuentra todos los usuarios cuya fecha de eliminación programada ({@code fechaEliminacion})
     * es anterior o igual a la fecha y hora actuales.
     * Utilizado por el servicio de limpieza de cuentas.
     *
     * @param currentTime La fecha y hora actual para la comparación.
     * @return Una lista de usuarios {@link User} programados para eliminación.
     */
    @Query("SELECT u FROM User u WHERE u.fechaEliminacion IS NOT NULL AND u.fechaEliminacion <= :currentTime")
    List<User> findUsersScheduledForDeletion(@Param("currentTime") OffsetDateTime currentTime);

    /**
     * Busca usuarios cuyo nombre de usuario contenga la cadena de consulta especificada (ignorando mayúsculas/minúsculas).
     * Excluye al usuario actual de los resultados de su propia búsqueda.
     * Solo considera usuarios que no estén marcados para eliminación ({@code fechaEliminacion} es nulo).
     *
     * @param query La cadena de texto a buscar dentro de los nombres de usuario.
     * @param currentUserId El ID interno del usuario que realiza la búsqueda, para excluirlo de los resultados.
     * @return Una lista de usuarios {@link User} que coinciden con el criterio de búsqueda.
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.nombreUsuario) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "AND u.fechaEliminacion IS NULL " +
            "AND u.id <> :currentUserId")
    List<User> searchByNombreUsuarioContainingIgnoreCaseAndNotSelf(
            @Param("query") String query,
            @Param("currentUserId") Long currentUserId
    );
}