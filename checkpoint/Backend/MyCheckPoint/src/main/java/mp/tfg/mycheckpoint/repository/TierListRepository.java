package mp.tfg.mycheckpoint.repository;

import mp.tfg.mycheckpoint.dto.enums.TierListType;
import mp.tfg.mycheckpoint.entity.GameList;
import mp.tfg.mycheckpoint.entity.TierList;
import mp.tfg.mycheckpoint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de Spring Data JPA para la entidad {@link TierList}.
 * Proporciona métodos para gestionar las Tier Lists, incluyendo consultas personalizadas
 * para cargar Tier Lists con sus secciones e ítems de manera eficiente.
 */
@Repository
public interface TierListRepository extends JpaRepository<TierList, Long> {

    /**
     * Busca una Tier List por su ID público (UUID).
     *
     * @param publicId El ID público de la Tier List.
     * @return Un {@link Optional} que contiene la {@link TierList} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<TierList> findByPublicId(UUID publicId);

    /**
     * Busca una Tier List que esté asociada a una {@link GameList} específica y sea de un tipo particular.
     * Útil para encontrar Tier Lists generadas automáticamente a partir de GameLists.
     *
     * @param sourceGameList La {@link GameList} origen.
     * @param type El {@link TierListType} (ej. FROM_GAMELIST).
     * @return Un {@link Optional} que contiene la {@link TierList} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<TierList> findBySourceGameListAndType(GameList sourceGameList, TierListType type);

    /**
     * Busca una Tier List por su ID público y propietario, cargando explícitamente
     * sus secciones ({@code sections}) ordenadas.
     *
     * @param publicId El ID público (UUID) de la Tier List.
     * @param owner El {@link User} propietario.
     * @return Un {@link Optional} con la {@link TierList} y sus secciones cargadas si se encuentra.
     */
    @Query("SELECT DISTINCT tl FROM TierList tl LEFT JOIN FETCH tl.sections s WHERE tl.publicId = :publicId AND tl.owner = :owner ORDER BY s.sectionOrder ASC")
    Optional<TierList> findByPublicIdAndOwnerWithSections(@Param("publicId") UUID publicId, @Param("owner") User owner);

    /**
     * Busca una Tier List pública por su ID público, cargando explícitamente
     * sus secciones ({@code sections}) ordenadas.
     *
     * @param publicId El ID público (UUID) de la Tier List.
     * @return Un {@link Optional} con la {@link TierList} pública y sus secciones cargadas si se encuentra.
     */
    @Query("SELECT DISTINCT tl FROM TierList tl LEFT JOIN FETCH tl.sections s WHERE tl.publicId = :publicId AND tl.isPublic = true ORDER BY s.sectionOrder ASC")
    Optional<TierList> findByPublicIdAndIsPublicTrueWithSections(@Param("publicId") UUID publicId);

    /**
     * Encuentra todas las Tier Lists de un propietario específico, cargando explícitamente sus secciones.
     * Las listas se ordenan por fecha de última actualización (descendente) y luego por el orden de sus secciones.
     *
     * @param owner El {@link User} propietario.
     * @return Una lista de {@link TierList} con sus secciones cargadas.
     */
    @Query("SELECT DISTINCT tl FROM TierList tl LEFT JOIN FETCH tl.sections s WHERE tl.owner = :owner ORDER BY tl.updatedAt DESC, s.sectionOrder ASC")
    List<TierList> findAllByOwnerAndTypeWithSections(@Param("owner") User owner);

    /**
     * Encuentra todas las Tier Lists que son públicas, cargando explícitamente sus secciones.
     * Las listas se ordenan por fecha de última actualización (descendente) y luego por el orden de sus secciones.
     *
     * @return Una lista de {@link TierList} públicas con sus secciones cargadas.
     */
    @Query("SELECT DISTINCT tl FROM TierList tl LEFT JOIN FETCH tl.sections s WHERE tl.isPublic = true ORDER BY tl.updatedAt DESC, s.sectionOrder ASC")
    List<TierList> findAllByIsPublicTrueAndFetchSections();

    List<TierList> findByOwnerAndType(User user, TierListType tierListType);

    Optional<Object> findByPublicIdAndOwner(UUID tierListPublicId, User owner);
}