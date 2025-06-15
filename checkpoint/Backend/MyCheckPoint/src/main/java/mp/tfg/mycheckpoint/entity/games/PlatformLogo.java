package mp.tfg.mycheckpoint.entity.games;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Objects;

/**
 * Clase embebible que representa el logo de una plataforma de videojuegos.
 * Contiene el ID del logo de una fuente externa y su URL.
 * Esta clase se integra dentro de la entidad {@link Platform}.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlatformLogo {

    /**
     * ID del logo proveniente de una fuente externa (ej. IGDB).
     */
    @Column(name = "logo_igdb_id")
    private Long igdbId;

    /**
     * URL donde se puede acceder a la imagen del logo de la plataforma.
     * La longitud máxima es de 512 caracteres.
     */
    @Column(name = "logo_url", length = 512)
    private String url;

    /**
     * Compara este PlatformLogo con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code igdbId} y la {@code url}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlatformLogo that = (PlatformLogo) o;
        return Objects.equals(igdbId, that.igdbId) && Objects.equals(url, that.url);
    }

    /**
     * Genera un código hash para este PlatformLogo.
     * El hash se basa en el {@code igdbId} y la {@code url}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(igdbId, url);
    }
}
