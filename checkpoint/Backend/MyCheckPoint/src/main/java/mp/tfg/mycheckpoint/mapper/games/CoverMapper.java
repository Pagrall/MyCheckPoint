package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.CoverDto;
import mp.tfg.mycheckpoint.entity.games.Cover;
import org.mapstruct.Mapper;

import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad embebible {@link Cover} y su DTO {@link CoverDto}.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface CoverMapper {

    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    CoverMapper INSTANCE = Mappers.getMapper(CoverMapper.class);

    /**
     * Convierte una entidad {@link Cover} a un {@link CoverDto}.
     * Mapea automáticamente los campos {@code igdbId} y {@code url}
     * ya que tienen el mismo nombre en ambas clases.
     *
     * @param cover La entidad Cover a convertir.
     * @return El CoverDto resultante.
     */
    CoverDto toDto(Cover cover);

    /**
     * Convierte un {@link CoverDto} a una entidad {@link Cover}.
     * Mapea automáticamente los campos {@code igdbId} y {@code url}.
     *
     * @param coverDto El CoverDto a convertir.
     * @return La entidad Cover resultante.
     */
    Cover toEntity(CoverDto coverDto);
}