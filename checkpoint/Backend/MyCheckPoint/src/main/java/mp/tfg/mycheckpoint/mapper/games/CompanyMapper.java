package mp.tfg.mycheckpoint.mapper.games;


import mp.tfg.mycheckpoint.dto.games.CompanyInfoDto;
import mp.tfg.mycheckpoint.entity.games.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad {@link Company} y su DTO {@link CompanyInfoDto}.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface CompanyMapper {
    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    /**
     * Convierte una entidad {@link Company} a un {@link CompanyInfoDto}.
     *
     * @param company La entidad Company a convertir.
     * @return El CompanyInfoDto resultante.
     */
    CompanyInfoDto toDto(Company company);

    /**
     * Convierte un {@link CompanyInfoDto} a una entidad {@link Company}.
     * Ignora el ID interno y la colección de involucramientos, ya que estos
     * se gestionan en otros niveles o por JPA.
     *
     * @param companyInfoDto El CompanyInfoDto a convertir.
     * @return La entidad Company resultante.
     */
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "involvements", ignore = true)
    Company toEntity(CompanyInfoDto companyInfoDto);
}