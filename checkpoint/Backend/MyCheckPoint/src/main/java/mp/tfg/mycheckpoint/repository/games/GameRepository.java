package mp.tfg.mycheckpoint.repository.games;

import mp.tfg.mycheckpoint.entity.games.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Game}.
 * Proporciona métodos CRUD básicos y la capacidad de definir consultas personalizadas
 * para acceder a los datos de los videojuegos.
 */
@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    /**
     * Busca un juego por su ID de IGDB.
     * Este método es útil para verificar si un juego obtenido de una fuente externa
     * ya existe en la base de datos local.
     *
     * @param igdbId El ID del juego en IGDB.
     * @return Un {@link Optional} que contiene el {@link Game} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<Game> findByIgdbId(Long igdbId);
}