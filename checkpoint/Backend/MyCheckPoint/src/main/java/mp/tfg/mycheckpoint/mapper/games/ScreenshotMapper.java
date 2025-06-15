package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.ScreenshotDto;
import mp.tfg.mycheckpoint.entity.games.Screenshot;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad embebible {@link Screenshot} y su DTO {@link ScreenshotDto}.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface ScreenshotMapper {

    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    ScreenshotMapper INSTANCE = Mappers.getMapper(ScreenshotMapper.class);

    /**
     * Convierte una entidad {@link Screenshot} a un {@link ScreenshotDto}.
     *
     * @param screenshot La entidad Screenshot a convertir.
     * @return El ScreenshotDto resultante.
     */
    ScreenshotDto toDto(Screenshot screenshot);

    /**
     * Convierte un {@link ScreenshotDto} a una entidad {@link Screenshot}.
     *
     * @param screenshotDto El ScreenshotDto a convertir.
     * @return La entidad Screenshot resultante.
     */
    Screenshot toEntity(ScreenshotDto screenshotDto);
}
