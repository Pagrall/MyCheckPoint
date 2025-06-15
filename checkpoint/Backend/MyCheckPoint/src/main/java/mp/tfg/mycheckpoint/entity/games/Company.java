package mp.tfg.mycheckpoint.entity.games;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * Entidad que representa una compañía de videojuegos (desarrolladora, editora, etc.).
 * Almacena información básica de la compañía y su relación con los juegos.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "companies")
public class Company {

    /**
     * Identificador interno único de la compañía en la base de datos local (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    /**
     * ID único de la compañía proveniente de una fuente externa (ej. IGDB).
     * Este ID debe ser único.
     */
    @Column(unique = true, nullable = false)
    private Long igdbId;

    /**
     * Nombre de la compañía. No puede ser nulo.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Conjunto de relaciones de involucramiento de esta compañía en diferentes juegos.
     * Mapea la relación inversa con {@link GameCompanyInvolvement}.
     * La carga es perezosa (LAZY) y las operaciones de persistencia se propagan (CascadeType.ALL).
     */
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<GameCompanyInvolvement> involvements = new HashSet<>();

    /**
     * Constructor útil para crear una instancia de Company con su ID de IGDB y nombre.
     * @param igdbId El ID de la compañía en IGDB.
     * @param name El nombre de la compañía.
     */
    public Company(Long igdbId, String name) {
        this.igdbId = igdbId;
        this.name = name;
    }

    /**
     * Compara esta Company con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code igdbId}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(igdbId, company.igdbId);
    }

    /**
     * Genera un código hash para esta Company.
     * El hash se basa en el {@code igdbId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(igdbId);
    }
}
