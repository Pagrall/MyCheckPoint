package mp.tfg.mycheckpoint.mapper;

import mp.tfg.mycheckpoint.dto.usergame.UserGameDataDTO;
import mp.tfg.mycheckpoint.dto.usergame.UserGameResponseDTO;
import mp.tfg.mycheckpoint.entity.UserGame;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad {@link UserGame} y sus DTOs relacionados.
 * Se encarga de mapear los datos específicos del usuario para un juego.
 */
@Mapper(componentModel = "spring")
public interface UserGameMapper {

    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    UserGameMapper INSTANCE = Mappers.getMapper(UserGameMapper.class);

    /**
     * Convierte una entidad {@link UserGame} a un {@link UserGameResponseDTO}.
     * Mapea el ID de IGDB del juego asociado.
     *
     * @param userGame La entidad UserGame a convertir.
     * @return El UserGameResponseDTO resultante.
     */
    @Mapping(source = "internalId", target = "internalId")
    @Mapping(source = "game.igdbId", target = "gameIgdbId")
    @Mapping(source = "game.name", target = "gameName")
    @Mapping(source = "game.cover", target = "gameCover")
    UserGameResponseDTO toResponseDto(UserGame userGame);

    /**
     * Actualiza una entidad {@link UserGame} existente con los datos de un {@link UserGameDataDTO}.
     * Ignora propiedades nulas del DTO durante la actualización.
     * Campos como IDs, usuario, juego y timestamps son ignorados ya que se gestionan por separado
     * o automáticamente.
     *
     * @param dto El DTO con los datos de actualización.
     * @param entity La entidad UserGame a actualizar (anotada con {@link MappingTarget}).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "game", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true) // MapStruct lo actualiza por @UpdateTimestamp
    @Mapping(target = "deletedAt", ignore = true)
    void updateFromDto(UserGameDataDTO dto, @MappingTarget UserGame entity);
}