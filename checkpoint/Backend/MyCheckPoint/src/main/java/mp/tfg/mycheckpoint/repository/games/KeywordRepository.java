package mp.tfg.mycheckpoint.repository.games;


import mp.tfg.mycheckpoint.entity.games.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Keyword}.
 * Proporciona métodos CRUD básicos y la capacidad de definir consultas personalizadas
 * para acceder a los datos de las palabras clave asociadas a los videojuegos.
 * Nota: Idealmente, esta interfaz debería estar en el paquete 'repository.games'.
 */
@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    /**
     * Busca una palabra clave por su ID de IGDB.
     *
     * @param igdbId El ID de la palabra clave en IGDB.
     * @return Un {@link Optional} que contiene la {@link Keyword} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<Keyword> findByIgdbId(Long igdbId);
}
