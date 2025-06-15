package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.GameModeDto;
import mp.tfg.mycheckpoint.entity.games.GameMode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad {@link GameMode} y su DTO {@link GameModeDto}.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface GameModeMapper {

    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    GameModeMapper INSTANCE = Mappers.getMapper(GameModeMapper.class);

    /**
     * Convierte una entidad {@link GameMode} a un {@link GameModeDto}.
     *
     * @param gameMode La entidad GameMode a convertir.
     * @return El GameModeDto resultante.
     */
    GameModeDto toDto(GameMode gameMode);

    /**
     * Convierte un {@link GameModeDto} a una entidad {@link GameMode}.
     * Ignora el ID interno (generado por la BDD) y la colección de juegos
     * (relación bidireccional gestionada por JPA).
     *
     * @param gameModeDto El GameModeDto a convertir.
     * @return La entidad GameMode resultante.
     */
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "games", ignore = true)
    GameMode toEntity(GameModeDto gameModeDto);
}