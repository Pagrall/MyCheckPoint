package mp.tfg.mycheckpoint.mapper;

import mp.tfg.mycheckpoint.dto.user.UserCreateDTO;
import mp.tfg.mycheckpoint.dto.user.UserDTO;
import mp.tfg.mycheckpoint.dto.user.UserProfileUpdateDTO;
import mp.tfg.mycheckpoint.dto.user.UserSearchResultDTO;
import mp.tfg.mycheckpoint.entity.User;
import org.mapstruct.*;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Mapper para la conversión entre entidades {@link User} y sus correspondientes DTOs.
 * Utiliza MapStruct para la generación automática del código de mapeo.
 * Configurado para ignorar propiedades nulas durante las actualizaciones.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    /**
     * Convierte una entidad {@link User} a un {@link UserDTO}.
     *
     * @param user La entidad User a convertir.
     * @return El UserDTO resultante.
     */
    UserDTO toDto(User user);

    /**
     * Convierte un {@link UserCreateDTO} a una entidad {@link User}.
     * Ignora campos que no deben establecerse durante la creación inicial o que son generados.
     *
     * @param userCreateDTO El DTO con los datos para crear el usuario.
     * @return La entidad User resultante, lista para ser persistida (contraseña aún sin hashear).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "fechaEliminacion", ignore = true)
    @Mapping(target = "contraseña", ignore = true) // La contraseña se hashea en el servicio
    @Mapping(target = "emailVerified", ignore = true) // Se maneja por separado
    User toEntity(UserCreateDTO userCreateDTO);

    /**
     * Actualiza una entidad {@link User} existente con los datos de un {@link UserProfileUpdateDTO}.
     * Solo los campos no nulos en el DTO se utilizarán para actualizar la entidad.
     *
     * @param userProfileUpdateDTO El DTO con los datos de actualización del perfil.
     * @param userEntity La entidad User a actualizar (anotada con {@link MappingTarget}).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfileFromDto(UserProfileUpdateDTO userProfileUpdateDTO, @MappingTarget User userEntity);

    /**
     * Convierte una entidad {@link User} a un {@link UserSearchResultDTO},
     * que contiene información básica para mostrar en resultados de búsqueda.
     *
     * @param user La entidad User a convertir.
     * @return El UserSearchResultDTO resultante.
     */
    @Mapping(source = "publicId", target = "publicId")
    @Mapping(source = "nombreUsuario", target = "nombreUsuario")
    @Mapping(source = "fotoPerfil", target = "fotoPerfil")
    UserSearchResultDTO toSearchResultDto(User user);

    /**
     * Convierte una lista de entidades {@link User} a una lista de {@link UserSearchResultDTO}.
     *
     * @param users La lista de entidades User a convertir.
     * @return La lista de UserSearchResultDTO resultante.
     */
    List<UserSearchResultDTO> toSearchResultDtoList(List<User> users);
}