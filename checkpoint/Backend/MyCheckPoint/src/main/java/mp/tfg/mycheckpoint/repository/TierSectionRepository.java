package mp.tfg.mycheckpoint.repository;

import mp.tfg.mycheckpoint.entity.TierList;
import mp.tfg.mycheckpoint.entity.TierSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de Spring Data JPA para la entidad {@link TierSection}.
 * Gestiona la persistencia de las secciones (tiers) dentro de una {@link TierList}.
 */
@Repository
public interface TierSectionRepository extends JpaRepository<TierSection, Long> {

    /**
     * Encuentra todas las secciones de una {@link TierList} específica,
     * ordenadas por su campo {@code sectionOrder} de forma ascendente.
     *
     * @param tierList La {@link TierList} de la cual obtener las secciones.
     * @return Una lista de {@link TierSection} ordenadas.
     */
    List<TierSection> findByTierListOrderBySectionOrderAsc(TierList tierList);

    /**
     * Busca la sección especial "sin clasificar" dentro de una {@link TierList} específica.
     *
     * @param tierList La {@link TierList} en la que buscar la sección sin clasificar.
     * @return Un {@link Optional} que contiene la {@link TierSection} sin clasificar si existe,
     * o un Optional vacío si no.
     */
    Optional<TierSection> findByTierListAndIsDefaultUnclassifiedTrue(TierList tierList);

    /**
     * Busca una {@link TierSection} por su ID interno y el ID público de la {@link TierList} a la que pertenece.
     *
     * @param sectionId El ID interno de la TierSection.
     * @param tierListPublicId El ID público (UUID) de la TierList contenedora.
     * @return Un {@link Optional} con la TierSection si se encuentra.
     */
    @Query("SELECT ts FROM TierSection ts WHERE ts.internalId = :sectionId AND ts.tierList.publicId = :tierListPublicId")
    Optional<TierSection> findByInternalIdAndTierListPublicId(@Param("sectionId") Long sectionId, @Param("tierListPublicId") UUID tierListPublicId);

    /**
     * Busca la sección "sin clasificar" de una {@link TierList} específica,
     * cargando explícitamente sus ítems ({@code items}) asociados.
     *
     * @param tierList La {@link TierList} para la cual encontrar la sección sin clasificar.
     * @return Un {@link Optional} que contiene la {@link TierSection} sin clasificar con sus ítems cargados,
     * o un Optional vacío si no se encuentra.
     */
    @Query("SELECT ts FROM TierSection ts LEFT JOIN FETCH ts.items WHERE ts.tierList = :tierList AND ts.isDefaultUnclassified = true")
    Optional<TierSection> findUnclassifiedSectionWithItemsByTierList(@Param("tierList") TierList tierList);
}