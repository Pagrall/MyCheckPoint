package mp.tfg.mycheckpoint.entity.games;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidad que representa una franquicia de videojuegos.
 * Una franquicia puede agrupar múltiples juegos.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "franchises")
public class Franchise {

    /**
     * Identificador interno único de la franquicia en la base de datos local (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    /**
     * ID único de la franquicia proveniente de una fuente externa (ej. IGDB).
     * Este ID debe ser único.
     */
    @Column(unique = true, nullable = false)
    private Long igdbId;

    /**
     * Nombre de la franquicia. No puede ser nulo.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Conjunto de juegos que pertenecen a esta franquicia.
     * Mapea la relación inversa con la entidad {@link Game}.
     */
    @ManyToMany(mappedBy = "franchises")
    private Set<Game> games = new HashSet<>();

    /**
     * Constructor útil para crear una instancia de Franchise con su ID de IGDB y nombre.
     * @param igdbId El ID de la franquicia en IGDB.
     * @param name El nombre de la franquicia.
     */
    public Franchise(Long igdbId, String name) {
        this.igdbId = igdbId;
        this.name = name;
    }

    /**
     * Compara esta Franchise con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code igdbId} (ID de la fuente externa).
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Franchise franchise = (Franchise) o;
        return Objects.equals(igdbId, franchise.igdbId);
    }

    /**
     * Genera un código hash para esta Franchise.
     * El hash se basa en el {@code igdbId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(igdbId);
    }
}
