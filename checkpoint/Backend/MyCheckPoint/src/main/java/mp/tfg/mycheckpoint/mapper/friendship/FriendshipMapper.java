package mp.tfg.mycheckpoint.mapper.friendship; // O mp.tfg.mycheckpoint.mapper.friendship

import mp.tfg.mycheckpoint.dto.friendship.FriendshipResponseDTO;
import mp.tfg.mycheckpoint.entity.Friendship;
import mp.tfg.mycheckpoint.entity.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir entidades {@link Friendship} a {@link FriendshipResponseDTO}.
 * Se encarga de determinar la información del "otro" usuario en la relación
 * desde la perspectiva del usuario actual.
 */
@Mapper(componentModel = "spring")
public interface FriendshipMapper {

    /**
     * Convierte una entidad {@link Friendship} a un {@link FriendshipResponseDTO}.
     *
     * @param friendship La entidad Friendship a convertir.
     * @param currentUser El usuario actual (autenticado) para contextualizar la respuesta
     * (determinar quién es el "otro" usuario y si el usuario actual inició la solicitud).
     * @return El FriendshipResponseDTO resultante.
     */
    @Mapping(target = "friendshipId", source = "friendship.internalId")
    @Mapping(target = "userPublicId", expression = "java(getOtherUser(friendship, currentUser).getPublicId())")
    @Mapping(target = "username", expression = "java(getOtherUser(friendship, currentUser).getNombreUsuario())")
    @Mapping(target = "profilePictureUrl", expression = "java(getOtherUser(friendship, currentUser).getFotoPerfil())")
    @Mapping(target = "status", source = "friendship.status")
    @Mapping(target = "isInitiatedByCurrentUser", expression = "java(friendship.getRequester().getId().equals(currentUser.getId()))")
    @Mapping(target = "createdAt", source = "friendship.createdAt")
    @Mapping(target = "updatedAt", source = "friendship.updatedAt")
    FriendshipResponseDTO toDto(Friendship friendship, @Context User currentUser);

    /**
     * Método de utilidad para obtener el "otro" usuario en una relación de amistad,
     * dado el usuario actual.
     *
     * @param friendship La entidad de amistad.
     * @param currentUser El usuario actual.
     * @return El usuario que es el "otro" en la relación.
     */
    default User getOtherUser(Friendship friendship, User currentUser) {
        if (friendship.getRequester().getId().equals(currentUser.getId())) {
            return friendship.getReceiver();
        } else {
            return friendship.getRequester();
        }
    }
}
