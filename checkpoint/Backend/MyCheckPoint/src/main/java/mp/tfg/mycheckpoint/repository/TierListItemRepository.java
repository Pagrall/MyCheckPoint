package mp.tfg.mycheckpoint.repository;

import mp.tfg.mycheckpoint.entity.TierList;
import mp.tfg.mycheckpoint.entity.TierListItem;
import mp.tfg.mycheckpoint.entity.TierSection;
import mp.tfg.mycheckpoint.entity.UserGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de Spring Data JPA para la entidad {@link TierListItem}.
 * Gestiona la persistencia de los ítems (juegos) dentro de las secciones de una Tier List.
 */
@Repository
public interface TierListItemRepository extends JpaRepository<TierListItem, Long> {

    /**
     * Encuentra todos los ítems de una sección específica, ordenados por su {@code itemOrder}.
     *
     * @param tierSection La {@link TierSection} de la cual obtener los ítems.
     * @return Una lista de {@link TierListItem} ordenados.
     */
    List<TierListItem> findByTierSectionOrderByItemOrderAsc(TierSection tierSection);

    /**
     * Busca un ítem específico dentro de una sección que corresponda a un {@link UserGame} particular.
     * Útil para verificar si un juego ya está en una sección.
     *
     * @param tierSection La {@link TierSection} donde buscar.
     * @param userGame El {@link UserGame} a buscar.
     * @return Un {@link Optional} que contiene el {@link TierListItem} si se encuentra,
     * o un Optional vacío si no.
     */
    Optional<TierListItem> findByTierSectionAndUserGame(TierSection tierSection, UserGame userGame);

    /**
     * Busca un {@link TierListItem} por su ID interno y el ID público de la {@link TierList} a la que pertenece.
     *
     * @param tierListItemId El ID interno del TierListItem.
     * @param tierListPublicId El ID público (UUID) de la TierList contenedora.
     * @return Un {@link Optional} con el TierListItem si se encuentra.
     */
    @Query("SELECT tli FROM TierListItem tli WHERE tli.internalId = :tierListItemId AND tli.tierSection.tierList.publicId = :tierListPublicId")
    Optional<TierListItem> findByInternalIdAndTierListPublicId(@Param("tierListItemId") Long tierListItemId, @Param("tierListPublicId") UUID tierListPublicId);

    /**
     * Comprueba si un {@link UserGame} específico ya existe como ítem en cualquier sección de una {@link TierList} dada.
     *
     * @param tierList La {@link TierList} en la que buscar.
     * @param userGame El {@link UserGame} a comprobar.
     * @return {@code true} si el UserGame ya existe como ítem en la TierList, {@code false} en caso contrario.
     */
    @Query("SELECT CASE WHEN COUNT(tli) > 0 THEN true ELSE false END " +
            "FROM TierListItem tli " +
            "WHERE tli.tierSection.tierList = :tierList AND tli.userGame = :userGame")
    boolean existsByTierListAndUserGame(@Param("tierList") TierList tierList, @Param("userGame") UserGame userGame);

    /**
     * Busca un {@link TierListItem} específico que corresponde a un {@link UserGame} dentro de una {@link TierList} dada.
     *
     * @param tierList La {@link TierList} en la que buscar.
     * @param userGame El {@link UserGame} a buscar.
     * @return Un {@link Optional} que contiene el {@link TierListItem} si se encuentra,
     * o un Optional vacío si no.
     */
    @Query("SELECT tli FROM TierListItem tli WHERE tli.tierSection.tierList = :tierList AND tli.userGame = :userGame")
    Optional<TierListItem> findByTierListAndUserGame(@Param("tierList") TierList tierList, @Param("userGame") UserGame userGame);

    /**
     * Elimina todos los {@link TierListItem} que corresponden a un {@link UserGame} específico
     * de todas las secciones de una {@link TierList} dada.
     * Este método es modificador y debe usarse dentro de una transacción.
     *
     * @param userGame El {@link UserGame} cuyos ítems serán eliminados.
     * @param tierList La {@link TierList} de la cual se eliminarán los ítems.
     */
    @Modifying
    @Query("DELETE FROM TierListItem tli WHERE tli.userGame = :userGame AND tli.tierSection.tierList = :tierList")
    void deleteAllByUserGameAndTierList(@Param("userGame") UserGame userGame, @Param("tierList") TierList tierList);

    /**
     * Encuentra el valor máximo de {@code itemOrder} para los ítems dentro de una {@link TierSection} específica.
     * Útil para determinar el siguiente orden al añadir un nuevo ítem al final.
     *
     * @param tierSection La {@link TierSection} para la cual encontrar el orden máximo.
     * @return Un {@link Optional} que contiene el orden máximo si la sección tiene ítems,
     * o un Optional vacío si la sección está vacía.
     */
    @Query("SELECT MAX(tli.itemOrder) FROM TierListItem tli WHERE tli.tierSection = :tierSection")
    Optional<Integer> findMaxItemOrderByTierSection(@Param("tierSection") TierSection tierSection);

    /**
     * Decrementa el {@code itemOrder} de todos los ítems en una {@link TierSection}
     * cuyo orden es mayor que el {@code itemOrder} especificado.
     * Útil cuando se elimina un ítem, para reajustar el orden de los ítems subsecuentes.
     * Este método es modificador y debe usarse dentro de una transacción.
     *
     * @param tierSection La {@link TierSection} afectada.
     * @param itemOrder El orden a partir del cual se decrementarán los órdenes.
     */
    @Modifying
    @Query("UPDATE TierListItem tli SET tli.itemOrder = tli.itemOrder - 1 WHERE tli.tierSection = :tierSection AND tli.itemOrder > :itemOrder")
    void decrementOrderOfSubsequentItems(@Param("tierSection") TierSection tierSection, @Param("itemOrder") int itemOrder);

    /**
     * Incrementa el {@code itemOrder} de todos los ítems en una {@link TierSection}
     * cuyo orden es mayor o igual al {@code itemOrder} especificado.
     * Útil cuando se inserta un nuevo ítem en una posición específica, para hacer espacio.
     * Este método es modificador y debe usarse dentro de una transacción.
     *
     * @param tierSection La {@link TierSection} afectada.
     * @param itemOrder El orden a partir del cual (inclusive) se incrementarán los órdenes.
     */
    @Modifying
    @Query("UPDATE TierListItem tli SET tli.itemOrder = tli.itemOrder + 1 WHERE tli.tierSection = :tierSection AND tli.itemOrder >= :itemOrder")
    void incrementOrderOfSubsequentItemsForInsert(@Param("tierSection") TierSection tierSection, @Param("itemOrder") int itemOrder);

    long countByUserGame(UserGame userGameToRemove);

    List<TierListItem> findByUserGame(UserGame userGameToRemove);
}