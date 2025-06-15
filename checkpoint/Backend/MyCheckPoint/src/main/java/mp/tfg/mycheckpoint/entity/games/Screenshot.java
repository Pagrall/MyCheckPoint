package mp.tfg.mycheckpoint.entity.games;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Objects;

/**
 * Clase embebible que representa una captura de pantalla (screenshot) de un juego.
 * Contiene el ID de la captura de una fuente externa y su URL.
 * Esta clase se integra directamente en la tabla de la entidad {@link Game}.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Screenshot {

    /**
     * ID de la captura de pantalla proveniente de una fuente externa (ej. IGDB).
     */
    @Column(name = "screenshot_igdb_id")
    private Long igdbId;

    /**
     * URL donde se puede acceder a la imagen de la captura de pantalla.
     * La longitud máxima es de 512 caracteres.
     */
    @Column(name = "screenshot_url", length = 512)
    private String url;

    /**
     * Compara este Screenshot con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code igdbId} y la {@code url}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Screenshot that = (Screenshot) o;
        return Objects.equals(igdbId, that.igdbId) && Objects.equals(url, that.url);
    }

    /**
     * Genera un código hash para este Screenshot.
     * El hash se basa en el {@code igdbId} y la {@code url}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(igdbId, url);
    }
}
