package mp.tfg.mycheckpoint.entity.games;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Objects;

/**
 * Clase embebible que representa una obra de arte (artwork) asociada a un juego.
 * Contiene el ID de la obra de arte de una fuente externa y su URL.
 * Esta clase se integra directamente en la tabla de la entidad {@link Game}.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Artwork {

    /**
     * ID de la obra de arte proveniente de una fuente externa (ej. IGDB).
     * Se utiliza para identificar de forma única la obra de arte.
     */
    @Column(name = "artwork_igdb_id")
    private Long igdbId;

    /**
     * URL donde se puede acceder a la imagen de la obra de arte.
     * La longitud máxima es de 512 caracteres.
     */
    @Column(name = "artwork_url", length = 512)
    private String url;

    /**
     * Compara este Artwork con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code igdbId} y la {@code url}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artwork artwork = (Artwork) o;
        return Objects.equals(igdbId, artwork.igdbId) &&
                Objects.equals(url, artwork.url);
    }

    /**
     * Genera un código hash para este Artwork.
     * El hash se basa en el {@code igdbId} y la {@code url}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(igdbId, url);
    }
}