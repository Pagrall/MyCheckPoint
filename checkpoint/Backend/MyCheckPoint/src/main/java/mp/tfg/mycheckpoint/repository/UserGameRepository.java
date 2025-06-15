package mp.tfg.mycheckpoint.repository;

import mp.tfg.mycheckpoint.dto.enums.VisibilidadEnum;
import mp.tfg.mycheckpoint.entity.User;
import mp.tfg.mycheckpoint.entity.UserGame;
import mp.tfg.mycheckpoint.entity.games.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link UserGame}.
 * Gestiona la persistencia de las entradas de la biblioteca de juegos de los usuarios,
 * que representan la relación específica de un usuario con un juego.
 */
@Repository
public interface UserGameRepository extends JpaRepository<UserGame, Long> {

    /**
     * Busca una entrada específica en la biblioteca de un usuario para un juego particular.
     *
     * @param user El {@link User} propietario de la entrada.
     * @param game El {@link Game} al que se refiere la entrada.
     * @return Un {@link Optional} que contiene la {@link UserGame} si existe,
     * o un Optional vacío si no.
     */
    Optional<UserGame> findByUserAndGame(User user, Game game);

    /**
     * Encuentra todas las entradas de la biblioteca de juegos para un usuario específico.
     *
     * @param user El {@link User} cuya biblioteca se va a recuperar.
     * @return Una lista de {@link UserGame} pertenecientes al usuario.
     */
    List<UserGame> findByUser(User user);

    /**
     * Busca una entrada en la biblioteca de un usuario para un juego, utilizando el ID de IGDB del juego.
     *
     * @param user El {@link User} propietario.
     * @param igdbId El ID de IGDB del {@link Game}.
     * @return Un {@link Optional} que contiene la {@link UserGame} si existe,
     * o un Optional vacío si no.
     */
    Optional<UserGame> findByUserAndGame_IgdbId(User user, Long igdbId);

    /**
     * Comprueba si existe una entrada en la biblioteca de un usuario para un juego con un ID de IGDB específico.
     *
     * @param user El {@link User} propietario.
     * @param igdbId El ID de IGDB del {@link Game}.
     * @return {@code true} si la entrada existe, {@code false} en caso contrario.
     */
    boolean existsByUserAndGame_IgdbId(User user, Long igdbId);

    /**
     * Elimina una entrada de la biblioteca de un usuario para un juego con un ID de IGDB específico.
     * Este método es transaccional y modificador.
     *
     * @param user El {@link User} propietario.
     * @param igdbId El ID de IGDB del {@link Game} a eliminar de la biblioteca del usuario.
     */
    void deleteByUserAndGame_IgdbId(User user, Long igdbId);

    /**
     * Encuentra todos los comentarios públicos para un juego específico.
     * Un comentario se considera público si la entrada {@link UserGame} tiene un comentario no nulo y no vacío,
     * y el perfil del usuario propietario del comentario es público ({@link VisibilidadEnum#PUBLICO}).
     * Los comentarios se ordenan por la fecha de última actualización de forma descendente.
     *
     * @param game El {@link Game} para el cual se buscan comentarios.
     * @return Una lista de {@link UserGame} que contienen comentarios públicos para el juego especificado.
     */
    @Query("SELECT ug FROM UserGame ug JOIN FETCH ug.user u " +
            "WHERE ug.game = :game " +
            "AND ug.comment IS NOT NULL AND ug.comment <> '' " +
            "AND u.fechaEliminacion IS NULL " +
            "ORDER BY ug.updatedAt DESC")

    List<UserGame> findPublicCommentsForGame(@Param("game") Game game);
}
