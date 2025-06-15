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
 * Entidad que representa un género de videojuego (ej. RPG, Acción, Estrategia).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "genres")
public class Genre {

    /**
     * Identificador interno único del género en la base de datos local (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    /**
     * ID único del género proveniente de una fuente externa (ej. IGDB).
     * Este ID debe ser único.
     */
    @Column(unique = true, nullable = false)
    private Long igdbId;

    /**
     * Nombre del género. No puede ser nulo.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Conjunto de juegos que pertenecen a este género.
     * Mapea la relación inversa con la entidad {@link Game}.
     */
    @ManyToMany(mappedBy = "genres")
    private Set<Game> games = new HashSet<>();

    /**
     * Constructor útil para crear una instancia de Genre con su ID de IGDB y nombre.
     * @param igdbId El ID del género en IGDB.
     * @param name El nombre del género.
     */
    public Genre(Long igdbId, String name) {
        this.igdbId = igdbId;
        this.name = name;
    }

    /**
     * Compara este Genre con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code igdbId}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return Objects.equals(igdbId, genre.igdbId);
    }

    /**
     * Genera un código hash para este Genre.
     * El hash se basa en el {@code igdbId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(igdbId);
    }
}