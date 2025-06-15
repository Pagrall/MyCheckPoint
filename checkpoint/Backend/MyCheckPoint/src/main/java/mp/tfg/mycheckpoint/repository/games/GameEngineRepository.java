package mp.tfg.mycheckpoint.repository.games;


import mp.tfg.mycheckpoint.entity.games.GameEngine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link GameEngine}.
 * Proporciona métodos CRUD básicos y la capacidad de definir consultas personalizadas
 * para acceder a los datos de los motores de videojuegos.
 */
@Repository
public interface GameEngineRepository extends JpaRepository<GameEngine, Long> {

    /**
     * Busca un motor de juego por su ID de IGDB.
     *
     * @param igdbId El ID del motor de juego en IGDB.
     * @return Un {@link Optional} que contiene el {@link GameEngine} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<GameEngine> findByIgdbId(Long igdbId);
}
