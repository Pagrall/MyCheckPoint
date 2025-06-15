package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.ThemeDto;
import mp.tfg.mycheckpoint.entity.games.Theme;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad {@link Theme} y su DTO {@link ThemeDto}.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface ThemeMapper {

    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    ThemeMapper INSTANCE = Mappers.getMapper(ThemeMapper.class);

    /**
     * Convierte una entidad {@link Theme} a un {@link ThemeDto}.
     *
     * @param theme La entidad Theme a convertir.
     * @return El ThemeDto resultante.
     */
    ThemeDto toDto(Theme theme);

    /**
     * Convierte un {@link ThemeDto} a una entidad {@link Theme}.
     * Ignora el ID interno y la colección de juegos.
     *
     * @param themeDto El ThemeDto a convertir.
     * @return La entidad Theme resultante.
     */
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "games", ignore = true)
    Theme toEntity(ThemeDto themeDto);
}