package mp.tfg.mycheckpoint.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mp.tfg.mycheckpoint.dto.enums.FriendshipStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Entidad que representa una relación de amistad o una solicitud de amistad entre dos usuarios.
 * La relación es direccional (de un {@code requester} a un {@code receiver}) y tiene un estado.
 * Se asegura la unicidad para evitar múltiples solicitudes/amistades activas entre los mismos dos usuarios.
 */
@Entity
@Table(name = "friendships", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"requester_user_internal_id", "receiver_user_internal_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {

    /**
     * Identificador interno único de la relación de amistad (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "internal_id")
    private Long internalId;

    /**
     * El usuario que envió la solicitud de amistad.
     * La carga es perezosa (LAZY). No puede ser nulo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_user_internal_id", nullable = false)
    private User requester;

    /**
     * El usuario que recibió la solicitud de amistad.
     * La carga es perezosa (LAZY). No puede ser nulo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_internal_id", nullable = false)
    private User receiver;

    /**
     * Estado actual de la relación de amistad (ej. PENDIENTE, ACEPTADA).
     * Se almacena como una cadena representando el nombre del enum {@link FriendshipStatus}. No puede ser nulo.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendshipStatus status;

    /**
     * Fecha y hora en que se creó la solicitud de amistad.
     * Generada automáticamente y no puede ser actualizada.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de esta relación (ej. cuando se aceptó).
     * Actualizada automáticamente en cada modificación.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Compara esta Friendship con otro objeto para determinar si son iguales.
     * La igualdad se basa en la identidad del {@code requester} y del {@code receiver}.
     * Es crucial para la lógica de negocio y para el funcionamiento de colecciones como {@code Set}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos representan la misma relación de amistad (mismos participantes),
     * {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        // Compara por los IDs de los usuarios involucrados para definir la unicidad de la relación.
        return Objects.equals(requester != null ? requester.getId() : null, that.requester != null ? that.requester.getId() : null) &&
                Objects.equals(receiver != null ? receiver.getId() : null, that.receiver != null ? that.receiver.getId() : null);
    }

    /**
     * Genera un código hash para esta Friendship.
     * El hash se basa en los IDs del {@code requester} y del {@code receiver}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                requester != null ? requester.getId() : null,
                receiver != null ? receiver.getId() : null
        );
    }
}
