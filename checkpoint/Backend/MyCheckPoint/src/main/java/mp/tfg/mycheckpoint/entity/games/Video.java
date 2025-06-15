package mp.tfg.mycheckpoint.entity.games;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Objects;

/**
 * Clase embebible que representa un vídeo asociado a un juego (ej. tráiler, gameplay).
 * Contiene el ID del vídeo de una fuente externa, su nombre, y el ID del vídeo en la plataforma
 * de origen (ej. ID de YouTube).
 * Esta clase se integra directamente en la tabla de la entidad {@link Game}.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    /**
     * ID del vídeo proveniente de una fuente externa (ej. IGDB).
     */
    @Column(name = "video_igdb_id")
    private Long igdbId;

    /**
     * Nombre o título del vídeo.
     */
    @Column(name = "video_name")
    private String name;

    /**
     * Identificador del vídeo en la plataforma de origen (ej. el ID de un vídeo de YouTube).
     */
    @Column(name = "video_platform_id")
    private String videoId;

    /**
     * Compara este Video con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code igdbId}, {@code name}, y {@code videoId}.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return Objects.equals(igdbId, video.igdbId) &&
                Objects.equals(name, video.name) &&
                Objects.equals(videoId, video.videoId);
    }

    /**
     * Genera un código hash para este Video.
     * El hash se basa en el {@code igdbId}, {@code name}, y {@code videoId}.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(igdbId, name, videoId);
    }
}
