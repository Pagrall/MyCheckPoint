package mp.tfg.mycheckpoint.repository.games;


import mp.tfg.mycheckpoint.entity.games.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Platform}.
 * Proporciona métodos CRUD básicos y la capacidad de definir consultas personalizadas
 * para acceder a los datos de las plataformas de videojuegos.
 */
@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

    /**
     * Busca una plataforma por su ID de IGDB.
     *
     * @param igdbId El ID de la plataforma en IGDB.
     * @return Un {@link Optional} que contiene la {@link Platform} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<Platform> findByIgdbId(Long igdbId);
}
