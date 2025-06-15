package mp.tfg.mycheckpoint.repository.games;


import mp.tfg.mycheckpoint.entity.games.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Company}.
 * Proporciona métodos CRUD básicos y la capacidad de definir consultas personalizadas
 * para acceder a los datos de las compañías de videojuegos.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * Busca una compañía por su ID de IGDB.
     *
     * @param igdbId El ID de la compañía en IGDB.
     * @return Un {@link Optional} que contiene la {@link Company} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<Company> findByIgdbId(Long igdbId);
}
