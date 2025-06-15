package mp.tfg.mycheckpoint.mapper.games;



import mp.tfg.mycheckpoint.dto.games.FranchiseDto;
import mp.tfg.mycheckpoint.entity.games.Franchise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para la conversión entre la entidad {@link Franchise} y su DTO {@link FranchiseDto}.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface FranchiseMapper {

    /**
     * Instancia del mapper para uso estático si no se inyecta.
     */
    FranchiseMapper INSTANCE = Mappers.getMapper(FranchiseMapper.class);

    /**
     * Convierte una entidad {@link Franchise} a un {@link FranchiseDto}.
     *
     * @param franchise La entidad Franchise a convertir.
     * @return El FranchiseDto resultante.
     */
    FranchiseDto toDto(Franchise franchise);

    /**
     * Convierte un {@link FranchiseDto} a una entidad {@link Franchise}.
     * Ignora el ID interno y la colección de juegos, ya que estos
     * se gestionan en otros niveles o por JPA.
     *
     * @param franchiseDto El FranchiseDto a convertir.
     * @return La entidad Franchise resultante.
     */
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "games", ignore = true) // Ignorar la colección inversa
    @Mapping(source = "name", target = "name")
    Franchise toEntity(FranchiseDto franchiseDto);
}
