package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.GenreDto;
import mp.tfg.mycheckpoint.entity.games.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad {@link Genre} y su DTO {@link GenreDto}.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface GenreMapper {

    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    GenreMapper INSTANCE = Mappers.getMapper(GenreMapper.class);

    /**
     * Convierte una entidad {@link Genre} a un {@link GenreDto}.
     *
     * @param genre La entidad Genre a convertir.
     * @return El GenreDto resultante.
     */
    GenreDto toDto(Genre genre);

    /**
     * Convierte un {@link GenreDto} a una entidad {@link Genre}.
     * Ignora el ID interno y la colección de juegos.
     *
     * @param genreDto El GenreDto a convertir.
     * @return La entidad Genre resultante.
     */
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "games", ignore = true)
    Genre toEntity(GenreDto genreDto);
}