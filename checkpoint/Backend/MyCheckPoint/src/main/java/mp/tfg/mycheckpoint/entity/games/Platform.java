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
 * Entidad que representa una plataforma de videojuegos (ej. PC, PlayStation 5, Xbox Series X).
 * Incluye el logo de la plataforma como un objeto embebido {@link PlatformLogo}.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "platforms")
public class Platform {

    /**
     * Identificador interno único de la plataforma en la base de datos local (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    /**
     * ID único de la plataforma proveniente de una fuente externa (ej. IGDB).
     * Este ID debe ser único.
     */
    @Column(unique = true, nullable = false)
    private Long igdbId;

    /**
     * Nombre alternativo o abreviatura de la plataforma. Puede ser nulo.
     */
    @Column(name = "alternative_name")
    private String alternativeName;

    /**
     * Nombre principal de la plataforma. No puede ser nulo.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Logo asociado a esta plataforma.
     * Se almacena como un objeto embebido {@link PlatformLogo}.
     */
    @Embedded
    private PlatformLogo platformLogo;

    /**
     * Conjunto de juegos disponibles en esta plataforma.
     * Mapea la relación inversa con la entidad {@link Game}.
     */
    @ManyToMany(mappedBy = "platforms")
    private Set<Game> games = new HashSet<>();

    /**
     * Constructor útil para crear una instancia de Platform.
     * @param igdbId El ID de la plataforma en IGDB.
     * @param name El nombre principal de la plataforma.
     * @param alternativeName El nombre alternativo de la plataforma.
     * @param platformLogo El logo de la plataforma.
     */
    public Platform(Long igdbId, String name, String alternativeName, PlatformLogo platformLogo) {
        this.igdbId = igdbId;
        this.name = name;
        this.alternativeName = alternativeName;
        this.platformLogo = platformLogo;
    }

    /**
     * Compara esta Platform con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code igdbId}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Platform platform = (Platform) o;
        return Objects.equals(igdbId, platform.igdbId);
    }

    /**
     * Genera un código hash para esta Platform.
     * El hash se basa en el {@code igdbId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(igdbId);
    }
}
