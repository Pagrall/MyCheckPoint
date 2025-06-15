package mp.tfg.mycheckpoint.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mp.tfg.mycheckpoint.dto.enums.UserGamePersonalPlatform;
import mp.tfg.mycheckpoint.dto.enums.UserGameStatus;
import mp.tfg.mycheckpoint.entity.games.Game;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Entidad que representa la relación entre un {@link User} y un {@link Game}.
 * Almacena los datos específicos que un usuario asocia a un juego en su biblioteca personal,
 * como el estado, puntuación, comentarios, plataforma personal, fechas de juego, etc.
 * La unicidad de esta entrada se define por la combinación de usuario y juego.
 */
@Entity
@Table(name = "user_games", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_internal_id", "game_internal_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGame {

    /**
     * Identificador interno único de esta entrada en la biblioteca del usuario (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "internal_id")
    private Long internalId;

    /**
     * El usuario al que pertenece esta entrada de la biblioteca.
     * La carga es perezosa (LAZY). No puede ser nulo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_internal_id", nullable = false)
    private User user;

    /**
     * El juego al que se refiere esta entrada de la biblioteca.
     * La carga es perezosa (LAZY). No puede ser nulo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_internal_id", nullable = false)
    private Game game;

    /**
     * Estado del juego según el usuario (ej. JUGANDO, COMPLETADO, EN LISTA DE DESEOS).
     * Se almacena como una cadena representando el nombre del enum {@link UserGameStatus}.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserGameStatus status;

    /**
     * Plataforma personal en la que el usuario juega o posee el juego.
     * Se almacena como una cadena representando el nombre del enum {@link UserGamePersonalPlatform}.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "personal_platform")
    private UserGamePersonalPlatform personalPlatform;

    /**
     * Indica si el usuario posee el juego (física o digitalmente).
     * Puede ser nulo si no se especifica.
     */
    @Column(name = "has_possession")
    private Boolean hasPossession;

    /**
     * Puntuación personal otorgada por el usuario al juego (ej. de 0.0 a 10.0).
     * Puede ser nulo si no se ha asignado puntuación.
     */
    @Column(name = "score")
    private Float score;

    /**
     * Comentario público del usuario sobre el juego. Almacenado como TEXT.
     * Puede ser nulo si no hay comentario.
     */
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    /**
     * Comentario privado del usuario sobre el juego, visible solo para él. Almacenado como TEXT.
     * Puede ser nulo si no hay comentario privado.
     */
    @Column(name = "private_comment", columnDefinition = "TEXT")
    private String privateComment;

    /**
     * Fecha en que el usuario comenzó a jugar el juego. Puede ser nula.
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * Fecha en que el usuario terminó de jugar el juego. Puede ser nula.
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Horas estimadas dedicadas a la historia principal del juego. Puede ser nulo.
     */
    @Column(name = "story_duration_hours")
    private Float storyDurationHours;

    /**
     * Horas estimadas dedicadas a la historia principal y contenido secundario. Puede ser nulo.
     */
    @Column(name = "story_secondary_duration_hours")
    private Float storySecondaryDurationHours;

    /**
     * Horas estimadas dedicadas para completar el juego al 100%. Puede ser nulo.
     */
    @Column(name = "completionist_duration_hours")
    private Float completionistDurationHours;

    /**
     * Fecha y hora de creación de esta entrada en la biblioteca.
     * Generada automáticamente y no puede ser actualizada.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de esta entrada.
     * Actualizada automáticamente en cada modificación.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Fecha y hora en que esta entrada fue marcada para borrado suave (soft delete).
     * Si es nulo, la entrada está activa. Puede ser utilizado para futuras implementaciones.
     */
    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    /**
     * Compara esta entrada UserGame con otro objeto para determinar si son iguales.
     * La igualdad se basa en la combinación del ID del usuario y el ID interno del juego,
     * asumiendo que estos identificadores son fiables para determinar la unicidad de la entrada
     * antes de la persistencia (si los IDs internos aún no están asignados).
     * Una vez persistido, la comparación por {@code internalId} sería más apropiada si se compara con otra entidad gestionada.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos representan la misma entrada de biblioteca, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserGame userGame = (UserGame) o;
        return Objects.equals(user != null ? user.getId() : null, userGame.user != null ? userGame.user.getId() : null) &&
                Objects.equals(game != null ? game.getInternalId() : null, userGame.game != null ? userGame.game.getInternalId() : null);
    }

    /**
     * Genera un código hash para esta entrada UserGame.
     * El hash se basa en los IDs del usuario y del juego.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(user != null ? user.getId() : null, game != null ? game.getInternalId() : null);
    }
}
