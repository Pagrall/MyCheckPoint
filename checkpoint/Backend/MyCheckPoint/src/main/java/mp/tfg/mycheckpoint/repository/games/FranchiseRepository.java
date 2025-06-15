package mp.tfg.mycheckpoint.repository.games;


import mp.tfg.mycheckpoint.entity.games.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Franchise}.
 * Proporciona métodos CRUD básicos y la capacidad de definir consultas personalizadas
 * para acceder a los datos de las franquicias de videojuegos.
 * Nota: Idealmente, esta interfaz debería estar en el paquete 'repository.games'.
 */
@Repository
public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

    /**
     * Busca una franquicia por su ID de IGDB.
     *
     * @param igdbId El ID de la franquicia en IGDB.
     * @return Un {@link Optional} que contiene la {@link Franchise} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<Franchise> findByIgdbId(Long igdbId);
}