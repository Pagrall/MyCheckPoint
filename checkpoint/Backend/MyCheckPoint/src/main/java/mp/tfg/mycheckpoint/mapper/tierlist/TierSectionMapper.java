package mp.tfg.mycheckpoint.mapper.tierlist;

import mp.tfg.mycheckpoint.dto.tierlist.TierSectionRequestDTO;
import mp.tfg.mycheckpoint.dto.tierlist.TierSectionResponseDTO;
import mp.tfg.mycheckpoint.entity.TierSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.BeanMapping;

import java.util.List;

/**
 * Mapper para la conversión entre la entidad {@link TierSection} y sus DTOs correspondientes.
 * Utiliza {@link TierListItemMapper} para mapear los ítems contenidos en la sección.
 */
@Mapper(componentModel = "spring", uses = {TierListItemMapper.class})
public interface TierSectionMapper {

    /**
     * Convierte una entidad {@link TierSection} a un {@link TierSectionResponseDTO}.
     * Mapea el ID interno, nombre, orden, si es la sección por defecto y la lista de ítems.
     *
     * @param tierSection La entidad TierSection a convertir.
     * @return El TierSectionResponseDTO resultante.
     */
    @Mapping(target = "internalId", source = "internalId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "color", source = "color")
    @Mapping(target = "order", source = "sectionOrder")
    @Mapping(target = "isDefaultUnclassified", source = "defaultUnclassified") // MapStruct infiere 'isDefaultUnclassified()' como 'defaultUnclassified'
    @Mapping(target = "items", source = "items") // Usa TierListItemMapper para los elementos
    TierSectionResponseDTO toResponseDTO(TierSection tierSection);

    /**
     * Convierte una lista de entidades {@link TierSection} a una lista de {@link TierSectionResponseDTO}.
     *
     * @param tierSections La lista de entidades TierSection a convertir.
     * @return La lista de TierSectionResponseDTO resultante.
     */
    List<TierSectionResponseDTO> toResponseDTOList(List<TierSection> tierSections);

    /**
     * Convierte un {@link TierSectionRequestDTO} a una entidad {@link TierSection} para creación.
     * Ignora campos que se gestionan automáticamente o se establecen en el servicio.
     *
     * @param requestDTO El DTO con los datos para crear la sección.
     * @return La entidad TierSection resultante.
     */
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "tierList", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "sectionOrder", ignore = true)
    @Mapping(target = "isDefaultUnclassified", ignore = true) // Se maneja en el servicio
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TierSection fromRequestDTO(TierSectionRequestDTO requestDTO);

    /**
     * Actualiza una entidad {@link TierSection} existente con los datos de un {@link TierSectionRequestDTO}.
     * Solo se actualiza el nombre. Otros campos son ignorados o gestionados por el servicio.
     *
     * @param requestDTO El DTO con el nuevo nombre para la sección.
     * @param tierSection La entidad TierSection a actualizar (anotada con {@link MappingTarget}).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "internalId", ignore = true)
    @Mapping(target = "tierList", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "sectionOrder", ignore = true)
    @Mapping(target = "defaultUnclassified", ignore = true) // Mapeo a 'isDefaultUnclassified' en la entidad
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromRequestDTO(TierSectionRequestDTO requestDTO, @MappingTarget TierSection tierSection);
}