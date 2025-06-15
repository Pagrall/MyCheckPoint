package mp.tfg.mycheckpoint.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad que representa una lista de juegos personalizada creada por un usuario.
 * Una lista puede contener múltiples juegos de la biblioteca del usuario ({@link UserGame}).
 * Puede ser pública o privada.
 */
@Entity
@Table(name = "game_lists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameList {

    /**
     * Identificador interno único de la lista de juegos (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "internal_id")
    private Long internalId;

    /**
     * Identificador público único de la lista de juegos (UUID).
     * Utilizado para acceder a la lista a través de APIs públicas sin exponer el ID interno.
     * Es generado automáticamente en la persistencia si es nulo, y no puede ser actualizado.
     */
    @Column(name = "public_id", unique = true, nullable = false, updatable = false, columnDefinition = "UUID")
    private UUID publicId;

    /**
     * El usuario propietario de esta lista de juegos.
     * La carga es perezosa (LAZY). No puede ser nulo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_internal_id", nullable = false)
    private User owner;

    /**
     * Nombre de la lista de juegos.
     * No puede ser nulo y tiene una longitud máxima de 150 caracteres.
     */
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    /**
     * Descripción opcional de la lista de juegos. Almacenado como TEXT.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Indica si la lista de juegos es pública o privada.
     * Por defecto es {@code false} (privada). No puede ser nulo.
     */
    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

    /**
     * Conjunto de entradas de la biblioteca del usuario ({@link UserGame}) que pertenecen a esta lista.
     * La relación es muchos a muchos, gestionada a través de la tabla de unión {@code game_list_user_games}.
     * La carga es perezosa (LAZY). Se inicializa por defecto a un HashSet vacío usando {@code @Builder.Default}.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "game_list_user_games",
            joinColumns = @JoinColumn(name = "game_list_internal_id"),
            inverseJoinColumns = @JoinColumn(name = "user_game_internal_id")
    )
    @Builder.Default // Asegura que la colección se inicialice si se usa el builder
    private Set<UserGame> userGames = new HashSet<>();

    /**
     * Fecha y hora de creación de la lista de juegos.
     * Generada automáticamente y no puede ser actualizada.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de la lista de juegos.
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
     * Compara esta GameList con otro objeto para determinar si son iguales.
     * Si el {@code internalId} no es nulo (entidad persistida), la igualdad se basa en él.
     * Si el {@code internalId} es nulo (entidad nueva), la igualdad se basa en el {@code publicId} si este no es nulo.
     * Como último recurso, se compara por identidad de instancia.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales según los criterios definidos, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameList gameList = (GameList) o;
        if (internalId != null && gameList.internalId != null) {
            return Objects.equals(internalId, gameList.internalId);
        }
        if (publicId != null && gameList.publicId != null) {
            return Objects.equals(publicId, gameList.publicId);
        }
        return this == o; // Fallback para entidades no persistidas sin publicId (raro)
    }

    /**
     * Genera un código hash para esta GameList.
     * Se basa en el {@code internalId} si no es nulo; de lo contrario, en el {@code publicId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(internalId == null ? publicId : internalId);
    }
}
