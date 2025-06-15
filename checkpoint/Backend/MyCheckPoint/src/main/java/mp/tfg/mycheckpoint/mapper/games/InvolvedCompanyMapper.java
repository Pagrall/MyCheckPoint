package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.InvolvedCompanyDto;
import mp.tfg.mycheckpoint.entity.games.GameCompanyInvolvement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad {@link GameCompanyInvolvement} y su DTO {@link InvolvedCompanyDto}.
 * Esta entidad representa la relación de una compañía con un juego (ej. desarrollador, editor).
 * Utiliza {@link CompanyMapper} para mapear la información de la compañía anidada.
 */
@Mapper(componentModel = "spring", uses = {CompanyMapper.class})
public interface InvolvedCompanyMapper {
    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    InvolvedCompanyMapper INSTANCE = Mappers.getMapper(InvolvedCompanyMapper.class);

    /**
     * Convierte una entidad {@link GameCompanyInvolvement} (relación de involucramiento) a un {@link InvolvedCompanyDto}.
     * MapStruct utilizará {@link CompanyMapper} para el campo {@code company}.
     *
     * @param involvement La entidad GameCompanyInvolvement a convertir.
     * @return El InvolvedCompanyDto resultante.
     */
    @Mapping(source = "involvementIgdbId", target = "involvementIgdbId")
    InvolvedCompanyDto toDto(GameCompanyInvolvement involvement);

    /**
     * Convierte un {@link InvolvedCompanyDto} a una entidad {@link GameCompanyInvolvement}.
     * Ignora el ID interno de la relación y la referencia al juego, ya que estos se gestionan
     * en la capa de servicio o por JPA al establecer la relación.
     * MapStruct utilizará {@link CompanyMapper} para el campo {@code company}.
     *
     * @param involvementDto El InvolvedCompanyDto a convertir.
     * @return La entidad GameCompanyInvolvement resultante.
     */
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "game", ignore = true) // El juego se establecerá en el servicio al crear la relación.
    @Mapping(source = "involvementIgdbId", target = "involvementIgdbId")
    GameCompanyInvolvement toEntity(InvolvedCompanyDto involvementDto);
}
