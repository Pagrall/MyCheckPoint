package mp.tfg.mycheckpoint.mapper.tierlist;

import mp.tfg.mycheckpoint.dto.tierlist.TierListCreateRequestDTO;
import mp.tfg.mycheckpoint.dto.tierlist.TierListResponseDTO;
import mp.tfg.mycheckpoint.dto.tierlist.TierListUpdateRequestDTO;
import mp.tfg.mycheckpoint.dto.tierlist.TierSectionResponseDTO;
import mp.tfg.mycheckpoint.entity.TierList;
import mp.tfg.mycheckpoint.entity.TierSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper abstracto para la conversión entre la entidad {@link TierList} y sus DTOs.
 * Utiliza {@link TierSectionMapper} para mapear las secciones contenidas.
 * La lógica para separar la sección "sin clasificar" de las secciones definidas por el usuario
 * se implementa en métodos concretos de esta clase abstracta.
 */
@Mapper(componentModel = "spring", uses = {TierSectionMapper.class})
public abstract class TierListMapper {

    /**
     * Inyección del mapper de secciones para ser utilizado en la lógica de mapeo personalizada.
     */
    @Autowired
    protected TierSectionMapper tierSectionMapper;

    /**
     * Mapeo base de {@link TierList} a {@link TierListResponseDTO}, excluyendo las secciones
     * que se manejarán en un método especializado.
     * @param tierList La entidad TierList a convertir.
     * @return Un TierListResponseDTO parcialmente poblado.
     */
    @Mapping(target = "publicId", source = "tierList.publicId")
    @Mapping(target = "name", source = "tierList.name")
    @Mapping(target = "description", source = "tierList.description")
    @Mapping(target = "type", source = "tierList.type")
    @Mapping(target = "sourceGameListPublicId", source = "tierList.sourceGameList.publicId")
    @Mapping(target = "ownerUsername", source = "tierList.owner.nombreUsuario")
    @Mapping(target = "isPublic", source = "public") // 'public' es la propiedad en la entidad debido a Lombok (isPublic())
    @Mapping(target = "createdAt", source = "tierList.createdAt")
    @Mapping(target = "updatedAt", source = "tierList.updatedAt")
    @Mapping(target = "sections", ignore = true) // Se manejan en toTierListResponseDTOWithSections
    @Mapping(target = "unclassifiedSection", ignore = true) // Se manejan en toTierListResponseDTOWithSections
    protected abstract TierListResponseDTO tierListToBaseDto(TierList tierList);

    /**
     * Convierte una entidad {@link TierList} completa, incluyendo sus secciones (separando la no clasificada),
     * a un {@link TierListResponseDTO}.
     * @param tierList La entidad TierList a convertir.
     * @return El TierListResponseDTO completamente poblado.
     */
    public TierListResponseDTO toTierListResponseDTOWithSections(TierList tierList) {
        if (tierList == null) {
            return null;
        }
        TierListResponseDTO responseDTO = tierListToBaseDto(tierList);
        mapSectionsLogic(tierList, responseDTO);
        return responseDTO;
    }

    /**
     * Lógica para mapear las secciones de una {@link TierList} a los campos correspondientes
     * en {@link TierListResponseDTO}, separando la sección de "ítems sin clasificar"
     * de las secciones definidas por el usuario.
     * @param tierList La entidad TierList fuente.
     * @param responseDTO El DTO de respuesta a poblar.
     */
    protected void mapSectionsLogic(TierList tierList, TierListResponseDTO responseDTO) {
        if (responseDTO == null) {
            return;
        }
        if (tierList == null || tierList.getSections() == null) {
            responseDTO.setSections(new ArrayList<>());
            responseDTO.setUnclassifiedSection(null);
            return;
        }
        if (this.tierSectionMapper == null) {
            responseDTO.setSections(new ArrayList<>());
            responseDTO.setUnclassifiedSection(null);
            return;
        }

        List<TierSectionResponseDTO> userDefinedSections = tierList.getSections().stream()
                .filter(s -> !s.isDefaultUnclassified())
                .map(this.tierSectionMapper::toResponseDTO) // Utiliza la instancia inyectada
                .collect(Collectors.toList());
        responseDTO.setSections(userDefinedSections);

        TierSectionResponseDTO unclassified = tierList.getSections().stream()
                .filter(TierSection::isDefaultUnclassified)
                .findFirst()
                .map(this.tierSectionMapper::toResponseDTO) // Utiliza la instancia inyectada
                .orElse(null);
        responseDTO.setUnclassifiedSection(unclassified);
        /*
        if (unclassified != null) {
            // System.out.println("TierListMapper - mapSectionsLogic: Mapeada sección sin clasificar: " + unclassified.getName());
        } else {
            // System.out.println("TierListMapper - mapSectionsLogic: No se encontró sección sin clasificar para mapear.");
        }
        // System.out.println("TierListMapper - FIN mapSectionsLogic para TierList ID: " + tierList.getPublicId());
        */
    }

    /**
     * Convierte un {@link TierListCreateRequestDTO} a una entidad {@link TierList} para creación.
     * Los campos como IDs, propietario, tipo, secciones y timestamps se gestionan en el servicio.
     * @param createRequestDTO El DTO con los datos para crear la Tier List.
     * @return La entidad TierList resultante.
     */
    public abstract TierList fromCreateRequestDTO(TierListCreateRequestDTO createRequestDTO);

    /**
     * Actualiza una entidad {@link TierList} existente con los datos de un {@link TierListUpdateRequestDTO}.
     * Solo los campos proporcionados (nombre, descripción, isPublic) se actualizan.
     * @param updateRequestDTO El DTO con los datos de actualización.
     * @param tierList La entidad TierList a actualizar (anotada con {@link MappingTarget}).
     */
    @Mapping(source = "isPublic", target = "public") // Mapeo para el campo booleano isPublic
    public abstract void updateFromUpdateRequestDTO(TierListUpdateRequestDTO updateRequestDTO, @MappingTarget TierList tierList);
}