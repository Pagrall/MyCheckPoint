package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.PlatformLogoDto;
import mp.tfg.mycheckpoint.entity.games.PlatformLogo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad embebible {@link PlatformLogo} y su DTO {@link PlatformLogoDto}.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface PlatformLogoMapper {
    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    PlatformLogoMapper INSTANCE = Mappers.getMapper(PlatformLogoMapper.class);

    /**
     * Convierte una entidad {@link PlatformLogo} a un {@link PlatformLogoDto}.
     *
     * @param platformLogo La entidad PlatformLogo a convertir.
     * @return El PlatformLogoDto resultante.
     */
    PlatformLogoDto toDto(PlatformLogo platformLogo);

    /**
     * Convierte un {@link PlatformLogoDto} a una entidad {@link PlatformLogo}.
     *
     * @param platformLogoDto El PlatformLogoDto a convertir.
     * @return La entidad PlatformLogo resultante.
     */
    PlatformLogo toEntity(PlatformLogoDto platformLogoDto);
}
