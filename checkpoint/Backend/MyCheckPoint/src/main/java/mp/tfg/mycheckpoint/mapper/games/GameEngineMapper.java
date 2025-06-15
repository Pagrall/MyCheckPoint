package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.GameEngineDto;
import mp.tfg.mycheckpoint.entity.games.GameEngine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad {@link GameEngine} y su DTO {@link GameEngineDto}.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface GameEngineMapper {

    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    GameEngineMapper INSTANCE = Mappers.getMapper(GameEngineMapper.class);

    /**
     * Convierte una entidad {@link GameEngine} a un {@link GameEngineDto}.
     *
     * @param gameEngine La entidad GameEngine a convertir.
     * @return El GameEngineDto resultante.
     */
    GameEngineDto toDto(GameEngine gameEngine);

    /**
     * Convierte un {@link GameEngineDto} a una entidad {@link GameEngine}.
     * Ignora el ID interno y la colección de juegos, ya que estos
     * se gestionan en otros niveles o por JPA.
     *
     * @param gameEngineDto El GameEngineDto a convertir.
     * @return La entidad GameEngine resultante.
     */
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "games", ignore = true)
    GameEngine toEntity(GameEngineDto gameEngineDto);
}