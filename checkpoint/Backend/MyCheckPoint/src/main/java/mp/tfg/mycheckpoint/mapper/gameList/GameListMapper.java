package mp.tfg.mycheckpoint.mapper.gameList;

import mp.tfg.mycheckpoint.dto.gameList.GameListRequestDTO;
import mp.tfg.mycheckpoint.dto.gameList.GameListResponseDTO;
import mp.tfg.mycheckpoint.entity.GameList;
import mp.tfg.mycheckpoint.mapper.UserGameMapper; // Asumiendo que tienes este mapper
import org.mapstruct.*;

/**
 * Mapper para la conversión entre entidades {@link GameList} y sus DTOs correspondientes.
 * Utiliza {@link UserGameMapper} para mapear la lista de juegos contenida.
 */
@Mapper(componentModel = "spring", uses = {UserGameMapper.class})
public interface GameListMapper {

    /**
     * Convierte un {@link GameListRequestDTO} a una entidad {@link GameList} para la creación.
     * Ignora campos gestionados por el sistema o establecidos en el servicio.
     *
     * @param requestDTO El DTO con los datos para crear la lista de juegos.
     * @return La entidad GameList resultante.
     */
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "publicId", ignore = true) // Se genera en @PrePersist o se maneja en servicio
    @Mapping(target = "owner", ignore = true) // Se establece en el servicio
    @Mapping(target = "userGames", ignore = true) // Se maneja por separado
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    GameList toEntity(GameListRequestDTO requestDTO);

    /**
     * Actualiza una entidad {@link GameList} existente con los datos de un {@link GameListRequestDTO}.
     * Ignora propiedades nulas del DTO y campos gestionados por el sistema.
     *
     * @param dto El DTO con los datos de actualización.
     * @param entity La entidad GameList a actualizar (anotada con {@link MappingTarget}).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "userGames", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "isPublic", target = "public") // 'public' es el nombre de la propiedad en la entidad debido a Lombok (isPublic() / setPublic())
    void updateFromDto(GameListRequestDTO dto, @MappingTarget GameList entity);


    /**
     * Convierte una entidad {@link GameList} a un {@link GameListResponseDTO}.
     * Mapea el nombre de usuario del propietario y cuenta el número de juegos.
     *
     * @param gameList La entidad GameList a convertir.
     * @return El GameListResponseDTO resultante.
     */
    @Mapping(source = "owner.nombreUsuario", target = "ownerUsername")
    @Mapping(source = "userGames", target = "gamesInList") // Usa UserGameMapper para los elementos
    @Mapping(target = "gameCount", expression = "java(gameList.getUserGames() != null ? gameList.getUserGames().size() : 0)")
    @Mapping(source = "public", target = "isPublic") // 'public' es el nombre de la propiedad en la entidad
    GameListResponseDTO toResponseDto(GameList gameList);

}