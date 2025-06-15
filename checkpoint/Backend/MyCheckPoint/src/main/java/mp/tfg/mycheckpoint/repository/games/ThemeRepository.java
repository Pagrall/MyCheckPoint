package mp.tfg.mycheckpoint.repository.games;



import mp.tfg.mycheckpoint.entity.games.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Theme}.
 * Proporciona métodos CRUD básicos y la capacidad de definir consultas personalizadas
 * para acceder a los datos de los temas de videojuegos.
 */
@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {

    /**
     * Busca un tema por su ID de IGDB.
     *
     * @param igdbId El ID del tema en IGDB.
     * @return Un {@link Optional} que contiene el {@link Theme} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<Theme> findByIgdbId(Long igdbId);
}
