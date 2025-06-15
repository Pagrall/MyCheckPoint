package mp.tfg.mycheckpoint.entity.games;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Objects;

/**
 * Clase embebible que representa un sitio web asociado a un juego (ej. sitio oficial, página de Steam).
 * Contiene el ID del sitio web de una fuente externa y su URL.
 * Esta clase se integra directamente en la tabla de la entidad {@link Game}.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Website {

    /**
     * ID del sitio web proveniente de una fuente externa (ej. IGDB).
     */
    @Column(name = "website_igdb_id")
    private Long igdbId;

    /**
     * URL completa del sitio web.
     * La longitud máxima es de 1024 caracteres.
     */
    @Column(name = "website_url", length = 1024)
    private String url;

    /**
     * Compara este Website con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code igdbId} y la {@code url}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Website website = (Website) o;
        return Objects.equals(igdbId, website.igdbId) && Objects.equals(url, website.url);
    }

    /**
     * Genera un código hash para este Website.
     * El hash se basa en el {@code igdbId} y la {@code url}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(igdbId, url);
    }
}