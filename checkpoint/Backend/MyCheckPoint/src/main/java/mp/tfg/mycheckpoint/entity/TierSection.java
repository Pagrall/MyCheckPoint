package mp.tfg.mycheckpoint.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa una sección (o "tier") dentro de una {@link TierList}.
 * Cada sección tiene un nombre, un orden y contiene una lista de {@link TierListItem} (juegos).
 * Una sección especial puede ser designada como el contenedor por defecto para ítems sin clasificar.
 */
@Entity
@Table(name = "tier_sections")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierSection {

    /**
     * Identificador interno único de la sección (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "internal_id")
    private Long internalId;

    /**
     * La {@link TierList} a la que pertenece esta sección.
     * La carga es perezosa (LAZY). No puede ser nulo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_list_internal_id", nullable = false)
    private TierList tierList;

    /**
     * Nombre de la sección (ej. "S Tier", "A Tier", "Juegos por Clasificar").
     * No puede ser nulo y tiene una longitud máxima de 100 caracteres.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Orden de esta sección dentro de la Tier List.
     * El valor 0 se reserva típicamente para la sección "Juegos por Clasificar".
     * Las secciones personalizables suelen empezar desde 1. No puede ser nulo.
     */
    @Column(name = "section_order", nullable = false)
    private int sectionOrder;

    /**
     * Indica si esta sección es la designada por defecto para los ítems
     * que aún no han sido clasificados por el usuario.
     * Por defecto es {@code false}. No puede ser nulo.
     */
    @Column(name = "is_default_unclassified", nullable = false)
    private boolean isDefaultUnclassified = false;

    /**
     * Color asociado a esta sección, utilizado para la representación visual.
     * Puede ser nulo si no se especifica un color.
     * La longitud máxima es de 20 caracteres.
     */
    @Column(name = "color", length = 7)
    private String color;

    /**
     * Lista de ítems (juegos) contenidos en esta sección.
     * La relación es uno a muchos. Las operaciones de persistencia se propagan (CascadeType.ALL)
     * y los ítems huérfanos se eliminan (orphanRemoval = true).
     * Los ítems se recuperan ordenados por {@code itemOrder}.
     * La carga es perezosa (LAZY). Se utiliza {@code FetchMode.SUBSELECT} para optimizar la carga de ítems
     * cuando se cargan múltiples secciones. Se inicializa por defecto a un ArrayList vacío.
     */
    @OneToMany(mappedBy = "tierSection", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("itemOrder ASC")
    @Builder.Default
    @Fetch(FetchMode.SUBSELECT)
    private List<TierListItem> items = new ArrayList<>();

    /**
     * Fecha y hora de creación de la sección.
     * Generada automáticamente y no puede ser actualizada.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de la sección.
     * Actualizada automáticamente en cada modificación.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Metodo helper para añadir un ítem a esta sección,
     * estableciendo la relación bidireccional.
     * @param item El {@link TierListItem} a añadir.
     */
    public void addItem(TierListItem item) {
        items.add(item);
        item.setTierSection(this);
    }

    /**
     * Método helper para eliminar un ítem de esta sección,
     * rompiendo la relación bidireccional.
     * @param item El {@link TierListItem} a eliminar.
     */
    public void removeItem(TierListItem item) {
        items.remove(item);
        item.setTierSection(null);
    }

    /**
     * Compara esta TierSection con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code internalId}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TierSection that = (TierSection) o;
        return Objects.equals(internalId, that.internalId);
    }

    /**
     * Genera un código hash para esta TierSection.
     * El hash se basa en el {@code internalId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(internalId);
    }
}