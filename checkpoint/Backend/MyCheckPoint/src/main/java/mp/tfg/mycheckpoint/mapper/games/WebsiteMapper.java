package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.WebsiteDto;
import mp.tfg.mycheckpoint.entity.games.Website;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad embebible {@link Website} y su DTO {@link WebsiteDto}.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface WebsiteMapper {

    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    WebsiteMapper INSTANCE = Mappers.getMapper(WebsiteMapper.class);

    /**
     * Convierte una entidad {@link Website} a un {@link WebsiteDto}.
     *
     * @param website La entidad Website a convertir.
     * @return El WebsiteDto resultante.
     */
    WebsiteDto toDto(Website website);

    /**
     * Convierte un {@link WebsiteDto} a una entidad {@link Website}.
     *
     * @param websiteDto El WebsiteDto a convertir.
     * @return La entidad Website resultante.
     */
    Website toEntity(WebsiteDto websiteDto);
}
