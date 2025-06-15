package mp.tfg.mycheckpoint.repository.games;



import mp.tfg.mycheckpoint.entity.games.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Genre}.
 * Proporciona métodos CRUD básicos y la capacidad de definir consultas personalizadas
 * para acceder a los datos de los géneros de videojuegos.
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Busca un género por su ID de IGDB.
     *
     * @param igdbId El ID del género en IGDB.
     * @return Un {@link Optional} que contiene el {@link Genre} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<Genre> findByIgdbId(Long igdbId);
}