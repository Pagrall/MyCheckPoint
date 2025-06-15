package mp.tfg.mycheckpoint.entity;

import jakarta.persistence.*;
import lombok.*;
import mp.tfg.mycheckpoint.dto.enums.TierListType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad que representa una Tier List.
 * Una Tier List es una clasificación jerárquica de ítems (generalmente juegos) en diferentes niveles o "tiers".
 * Puede ser de tipo general para el perfil del usuario o generada a partir de una {@link GameList} específica.
 */
@Entity
@Table(name = "tier_lists")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierList {

    /**
     * Identificador interno único de la Tier List (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "internal_id")
    private Long internalId;

    /**
     * Identificador público único de la Tier List (UUID).
     * Utilizado para acceder a la lista a través de APIs públicas.
     * Generado automáticamente en la persistencia si es nulo. No actualizable.
     */
    @Column(name = "public_id", unique = true, nullable = false, updatable = false, columnDefinition = "UUID")
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID publicId;

    /**
     * El usuario propietario de esta Tier List.
     * La carga es perezosa (LAZY). No puede ser nulo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_internal_id", nullable = false)
    private User owner;

    /**
     * Nombre de la Tier List.
     * No puede ser nulo y tiene una longitud máxima de 150 caracteres.
     */
    @Column(nullable = false, length = 150)
    private String name;

    /**
     * Descripción opcional de la Tier List. Almacenado como TEXT.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Tipo de Tier List (ej. PROFILE_GLOBAL, FROM_GAMELIST).
     * Se almacena como una cadena representando el nombre del enum {@link TierListType}. No puede ser nulo.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TierListType type;

    /**
     * La {@link GameList} origen, si esta Tier List es de tipo {@link TierListType#FROM_GAMELIST}.
     * Será nulo para Tier Lists de tipo {@link TierListType#PROFILE_GLOBAL}.
     * La carga es perezosa (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_game_list_internal_id", nullable = true)
    private GameList sourceGameList;

    /**
     * Indica si la Tier List es pública o privada.
     * Por defecto es {@code false} (privada). No puede ser nulo.
     */
    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

    /**
     * Lista de secciones (tiers) que componen esta Tier List.
     * La relación es uno a muchos. Las operaciones de persistencia se propagan (CascadeType.ALL)
     * y las secciones huérfanas se eliminan (orphanRemoval = true).
     * Las secciones se recuperan ordenadas por {@code sectionOrder}.
     * La carga es perezosa (LAZY). Se inicializa por defecto a un ArrayList vacío usando {@code @Builder.Default}.
     */
    @OneToMany(mappedBy = "tierList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sectionOrder ASC")
    @Builder.Default
    private List<TierSection> sections = new ArrayList<>();

    /**
     * Fecha y hora de creación de la Tier List.
     * Generada automáticamente y no puede ser actualizada.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de la Tier List.
     * Actualizada automáticamente en cada modificación.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Método de ciclo de vida JPA que asegura que {@code publicId} se genere
     * antes de que la entidad sea persistida por primera vez, si aún no tiene un valor.
     */
    @PrePersist
    protected void onPrePersist() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }

    /**
     * Método helper para añadir una sección a esta Tier List,
     * estableciendo la relación bidireccional.
     * @param section La {@link TierSection} a añadir.
     */
    public void addSection(TierSection section) {
        sections.add(section);
        section.setTierList(this);
    }

    /**
     * Método helper para eliminar una sección de esta Tier List,
     * rompiendo la relación bidireccional.
     * @param section La {@link TierSection} a eliminar.
     */
    public void removeSection(TierSection section) {
        sections.remove(section);
        section.setTierList(null);
    }

    /**
     * Compara esta TierList con otro objeto para determinar si son iguales.
     * La igualdad se basa primero en el {@code internalId} si ambos no son nulos.
     * Si los IDs internos son nulos (entidades no persistidas), se compara por {@code publicId} si ambos no son nulos.
     * Como último recurso, se compara por identidad de instancia.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TierList tierList = (TierList) o;
        if (internalId != null && tierList.internalId != null) {
            return Objects.equals(internalId, tierList.internalId);
        }
        if (publicId != null && tierList.publicId != null) {
            return Objects.equals(publicId, tierList.publicId);
        }
        return this == o;
    }

    /**
     * Genera un código hash para esta TierList.
     * Se basa en el {@code internalId} si no es nulo; de lo contrario, en el {@code publicId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(internalId == null ? publicId : internalId);
    }
}