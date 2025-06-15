package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.KeywordDto;
import mp.tfg.mycheckpoint.entity.games.Keyword;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad {@link Keyword} y su DTO {@link KeywordDto}.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface KeywordMapper {

    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    KeywordMapper INSTANCE = Mappers.getMapper(KeywordMapper.class);

    /**
     * Convierte una entidad {@link Keyword} a un {@link KeywordDto}.
     *
     * @param keyword La entidad Keyword a convertir.
     * @return El KeywordDto resultante.
     */
    KeywordDto toDto(Keyword keyword);

    /**
     * Convierte un {@link KeywordDto} a una entidad {@link Keyword}.
     * Ignora el ID interno y la colección inversa de juegos.
     *
     * @param keywordDto El KeywordDto a convertir.
     * @return La entidad Keyword resultante.
     */
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "games", ignore = true)
    Keyword toEntity(KeywordDto keywordDto);
}