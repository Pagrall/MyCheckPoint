package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.PlatformDto;

import mp.tfg.mycheckpoint.entity.games.Platform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad {@link Platform} y su DTO {@link PlatformDto}.
 * Utiliza {@link PlatformLogoMapper} para mapear el objeto {@code platformLogo} anidado.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring", uses = {PlatformLogoMapper.class})
public interface PlatformMapper {
    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    PlatformMapper INSTANCE = Mappers.getMapper(PlatformMapper.class);

    /**
     * Convierte una entidad {@link Platform} a un {@link PlatformDto}.
     * MapStruct utilizará automáticamente {@link PlatformLogoMapper} para el campo {@code platformLogo}.
     *
     * @param platform La entidad Platform a convertir.
     * @return El PlatformDto resultante.
     */
    PlatformDto toDto(Platform platform);

    /**
     * Convierte un {@link PlatformDto} a una entidad {@link Platform}.
     * Ignora el ID interno y la colección de juegos.
     * MapStruct utilizará automáticamente {@link PlatformLogoMapper} para el campo {@code platformLogo}.
     *
     * @param platformDto El PlatformDto a convertir.
     * @return La entidad Platform resultante.
     */
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "games", ignore = true)
    Platform toEntity(PlatformDto platformDto);
}