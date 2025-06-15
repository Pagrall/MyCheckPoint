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
 * Entidad que representa un modo de juego (ej. Un jugador, Multijugador, Cooperativo).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "game_modes")
public class GameMode {

    /**
     * Identificador interno único del modo de juego en la base de datos local (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    /**
     * ID único del modo de juego proveniente de una fuente externa (ej. IGDB).
     * Este ID debe ser único.
     */
    @Column(unique = true, nullable = false)
    private Long igdbId;

    /**
     * Nombre del modo de juego. No puede ser nulo.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Conjunto de juegos que tienen este modo de juego.
     * Mapea la relación inversa con la entidad {@link Game}.
     */
    @ManyToMany(mappedBy = "gameModes")
    private Set<Game> games = new HashSet<>();

    /**
     * Constructor útil para crear una instancia de GameMode con su ID de IGDB y nombre.
     * @param igdbId El ID del modo de juego en IGDB.
     * @param name El nombre del modo de juego.
     */
    public GameMode(Long igdbId, String name) {
        this.igdbId = igdbId;
        this.name = name;
    }

    /**
     * Compara este GameMode con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code igdbId}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameMode gameMode = (GameMode) o;
        return Objects.equals(igdbId, gameMode.igdbId);
    }

    /**
     * Genera un código hash para este GameMode.
     * El hash se basa en el {@code igdbId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(igdbId);
    }
}