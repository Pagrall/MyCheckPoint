package mp.tfg.mycheckpoint.entity.games;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Objects;

/**
 * Entidad que representa la relación de involucramiento entre un {@link Game} y una {@link Company}.
 * Especifica el rol de la compañía (desarrolladora, editora, etc.) en un juego particular.
 * La unicidad de la relación se basa en el {@code involvementIgdbId}.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "game_company_involvements", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"involvement_igdb_id"})
})
public class GameCompanyInvolvement {

    /**
     * Identificador interno único de esta relación de involucramiento (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    /**
     * ID único de esta relación de involucramiento, proveniente de una fuente externa (ej. IGDB).
     * Debe ser único y no nulo.
     */
    @Column(name = "involvement_igdb_id", unique = true, nullable = false)
    private Long involvementIgdbId;

    /**
     * El juego al que se refiere esta relación de involucramiento.
     * La relación es muchos a uno (muchos involucramientos pueden referirse a un juego).
     * No puede ser nulo. La carga es perezosa (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_internal_id", nullable = false)
    private Game game;

    /**
     * La compañía involucrada en esta relación.
     * La relación es muchos a uno (muchos involucramientos pueden referirse a una compañía).
     * No puede ser nula. La carga es perezosa (LAZY).
     * Las operaciones de persistencia y fusión se propagan a la entidad Company.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "company_internal_id", nullable = false)
    private Company company;

    /**
     * Indica si la compañía actuó como desarrolladora del juego.
     */
    private boolean developer;

    /**
     * Indica si la compañía actuó en la portabilidad (porting) del juego a otra plataforma.
     */
    private boolean porting;

    /**
     * Indica si la compañía actuó como editora (publisher) del juego.
     */
    private boolean publisher;

    /**
     * Indica si la compañía dio soporte (supporting) al desarrollo del juego.
     */
    private boolean supporting;

    /**
     * Compara este GameCompanyInvolvement con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code involvementIgdbId}, que es el ID de la relación
     * en la fuente externa y se considera la clave de negocio principal para esta entidad.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameCompanyInvolvement that = (GameCompanyInvolvement) o;
        return Objects.equals(involvementIgdbId, that.involvementIgdbId);
    }

    /**
     * Genera un código hash para este GameCompanyInvolvement.
     * El hash se basa en el {@code involvementIgdbId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(involvementIgdbId);
    }
}
