package mp.tfg.mycheckpoint.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Entidad que representa un ítem (generalmente un juego de la biblioteca del usuario)
 * dentro de una {@link TierSection} de una {@link TierList}.
 * Define la asociación de un {@link UserGame} a una sección específica y su orden dentro de ella.
 * La unicidad se define por la combinación de la sección y el UserGame.
 */
@Entity
@Table(name = "tier_list_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tier_section_internal_id", "user_game_internal_id"}, name = "uk_tierlistitem_section_usergame")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierListItem {

    /**
     * Identificador interno único del ítem en la Tier List (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "internal_id")
    private Long internalId;

    /**
     * La {@link TierSection} a la que pertenece este ítem.
     * La carga es perezosa (LAZY). No puede ser nulo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_section_internal_id", nullable = false)
    private TierSection tierSection;

    /**
     * La entrada de la biblioteca del usuario ({@link UserGame}) que representa este ítem.
     * La carga es perezosa (LAZY). No puede ser nulo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_game_internal_id", nullable = false)
    private UserGame userGame;

    /**
     * Orden de este ítem dentro de su sección en la Tier List.
     * Determina la posición visual del ítem. No puede ser nulo.
     */
    @Column(name = "item_order", nullable = false)
    private int itemOrder;

    /**
     * Fecha y hora de creación de este ítem en la Tier List.
     * Generada automáticamente y no puede ser actualizada.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de este ítem (ej. cambio de orden o sección).
     * Actualizada automáticamente en cada modificación.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Compara este TierListItem con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code internalId}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TierListItem that = (TierListItem) o;
        return Objects.equals(internalId, that.internalId);
    }

    /**
     * Genera un código hash para este TierListItem.
     * El hash se basa en el {@code internalId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(internalId);
    }
}