package mp.tfg.mycheckpoint.mapper.games;



import mp.tfg.mycheckpoint.dto.games.VideoDto;
import mp.tfg.mycheckpoint.entity.games.Video;
import org.mapstruct.Mapper;

import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad embebible {@link Video} y su DTO {@link VideoDto}.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface VideoMapper {

    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    VideoMapper INSTANCE = Mappers.getMapper(VideoMapper.class);

    /**
     * Convierte una entidad {@link Video} a un {@link VideoDto}.
     *
     * @param video La entidad Video a convertir.
     * @return El VideoDto resultante.
     */
    VideoDto toDto(Video video);

    /**
     * Convierte un {@link VideoDto} a una entidad {@link Video}.
     * El campo {@code videoId} del DTO (anotado con {@code @JsonProperty("video_id")})
     * se mapea al campo {@code videoId} de la entidad.
     *
     * @param videoDto El VideoDto a convertir.
     * @return La entidad Video resultante.
     */
    Video toEntity(VideoDto videoDto);
}
