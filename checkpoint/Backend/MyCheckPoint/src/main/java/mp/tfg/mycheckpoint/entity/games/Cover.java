package mp.tfg.mycheckpoint.entity.games;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Clase embebible que representa la información de la carátula (cover) de un juego.
 * Contiene el ID de la carátula de una fuente externa y su URL.
 * Esta clase se integra directamente en la tabla de la entidad {@link Game}.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cover {

    /**
     * ID de la carátula proveniente de una fuente externa (ej. IGDB).
     * Se utiliza para identificar de forma única la carátula.
     */
    @Column(name = "cover_igdb_id")
    private Long igdbId;

    /**
     * URL donde se puede acceder a la imagen de la carátula.
     */
    @Column(name = "cover_url")
    private String url;
}