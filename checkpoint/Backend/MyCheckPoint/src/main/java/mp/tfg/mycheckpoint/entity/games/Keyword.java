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
 * Entidad que representa una palabra clave (keyword) asociada a videojuegos.
 * Las palabras clave ayudan a categorizar y buscar juegos por temas o características específicas.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "keywords")
public class Keyword {

    /**
     * Identificador interno único de la palabra clave en la base de datos local (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    /**
     * ID único de la palabra clave proveniente de una fuente externa (ej. IGDB).
     * Este ID debe ser único.
     */
    @Column(unique = true, nullable = false)
    private Long igdbId;

    /**
     * Nombre de la palabra clave. No puede ser nulo.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Conjunto de juegos asociados con esta palabra clave.
     * Mapea la relación inversa con la entidad {@link Game}, donde 'keywords' es el campo propietario.
     */
    @ManyToMany(mappedBy = "keywords") // campo en la entidad Game
    private Set<Game> games = new HashSet<>();

    /**
     * Constructor útil para crear una instancia de Keyword con su ID de IGDB y nombre.
     * @param igdbId El ID de la palabra clave en IGDB.
     * @param name El nombre de la palabra clave.
     */
    public Keyword(Long igdbId, String name) {
        this.igdbId = igdbId;
        this.name = name;
    }

    /**
     * Compara esta Keyword con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code igdbId}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Keyword keyword = (Keyword) o;
        return Objects.equals(igdbId, keyword.igdbId);
    }

    /**
     * Genera un código hash para esta Keyword.
     * El hash se basa en el {@code igdbId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(igdbId);
    }
}
