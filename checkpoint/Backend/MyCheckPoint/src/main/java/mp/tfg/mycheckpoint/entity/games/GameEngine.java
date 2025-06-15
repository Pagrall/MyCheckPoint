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
 * Entidad que representa un motor de videojuego (Game Engine).
 * Los motores de juego son el software subyacente utilizado para crear juegos.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "game_engines")
public class GameEngine {

    /**
     * Identificador interno único del motor de juego en la base de datos local (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    /**
     * ID único del motor de juego proveniente de una fuente externa (ej. IGDB).
     * Este ID debe ser único.
     */
    @Column(unique = true, nullable = false)
    private Long igdbId;

    /**
     * Nombre del motor de juego. No puede ser nulo.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Conjunto de juegos que utilizan este motor de juego.
     * Mapea la relación inversa con la entidad {@link Game}.
     */
    @ManyToMany(mappedBy = "gameEngines") // campo en la entidad Game
    private Set<Game> games = new HashSet<>();

    /**
     * Constructor útil para crear una instancia de GameEngine con su ID de IGDB y nombre.
     * @param igdbId El ID del motor de juego en IGDB.
     * @param name El nombre del motor de juego.
     */
    public GameEngine(Long igdbId, String name) {
        this.igdbId = igdbId;
        this.name = name;
    }

    /**
     * Compara este GameEngine con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code igdbId}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameEngine that = (GameEngine) o;
        return Objects.equals(igdbId, that.igdbId);
    }

    /**
     * Genera un código hash para este GameEngine.
     * El hash se basa en el {@code igdbId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(igdbId);
    }
}