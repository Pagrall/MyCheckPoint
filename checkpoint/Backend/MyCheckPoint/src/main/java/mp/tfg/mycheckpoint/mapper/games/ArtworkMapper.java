package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.ArtworkDto;
import mp.tfg.mycheckpoint.entity.games.Artwork;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad embebible {@link Artwork} y su DTO {@link ArtworkDto}.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface ArtworkMapper {

    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    ArtworkMapper INSTANCE = Mappers.getMapper(ArtworkMapper.class);

    /**
     * Convierte una entidad {@link Artwork} a un {@link ArtworkDto}.
     *
     * @param artwork La entidad Artwork a convertir.
     * @return El ArtworkDto resultante.
     */
    ArtworkDto toDto(Artwork artwork);

    /**
     * Convierte un {@link ArtworkDto} a una entidad {@link Artwork}.
     *
     * @param artworkDto El ArtworkDto a convertir.
     * @return La entidad Artwork resultante.
     */
    Artwork toEntity(ArtworkDto artworkDto);
}