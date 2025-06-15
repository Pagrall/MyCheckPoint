package mp.tfg.mycheckpoint.repository.games;


import mp.tfg.mycheckpoint.entity.games.GameCompanyInvolvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


/**
 * Repositorio de Spring Data JPA para la entidad {@link GameCompanyInvolvement}.
 * Gestiona la persistencia de las relaciones de involucramiento entre juegos y compañías.
 */
@Repository
public interface GameCompanyInvolvementRepository extends JpaRepository<GameCompanyInvolvement, Long> {

    /**
     * Busca una relación de involucramiento de compañía por su ID de involucramiento de IGDB.
     *
     * @param involvementIgdbId El ID de la relación de involucramiento en IGDB.
     * @return Un {@link Optional} que contiene la {@link GameCompanyInvolvement} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<GameCompanyInvolvement> findByInvolvementIgdbId(Long involvementIgdbId);

}
