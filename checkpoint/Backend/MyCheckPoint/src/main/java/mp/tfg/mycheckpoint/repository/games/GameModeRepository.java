package mp.tfg.mycheckpoint.repository.games;

import mp.tfg.mycheckpoint.entity.games.GameMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link GameMode}.
 * Proporciona métodos CRUD básicos y la capacidad de definir consultas personalizadas
 * para acceder a los datos de los modos de juego.
 */
@Repository
public interface GameModeRepository extends JpaRepository<GameMode, Long> {

    /**
     * Busca un modo de juego por su ID de IGDB.
     *
     * @param igdbId El ID del modo de juego en IGDB.
     * @return Un {@link Optional} que contiene el {@link GameMode} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<GameMode> findByIgdbId(Long igdbId);
}