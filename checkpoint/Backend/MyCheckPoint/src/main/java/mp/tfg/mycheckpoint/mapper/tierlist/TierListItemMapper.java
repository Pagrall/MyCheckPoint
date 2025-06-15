package mp.tfg.mycheckpoint.mapper.tierlist;

import mp.tfg.mycheckpoint.dto.tierlist.TierListItemGameInfoDTO;
import mp.tfg.mycheckpoint.entity.TierListItem;
import mp.tfg.mycheckpoint.entity.games.Game; // Asegúrate que la entidad Game esté importada
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper para convertir entidades {@link TierListItem} a {@link TierListItemGameInfoDTO}.
 * Extrae información relevante del juego asociado para mostrarla en la Tier List.
 */
@Mapper(componentModel = "spring")
public interface TierListItemMapper {

    /**
     * Convierte una entidad {@link TierListItem} a un {@link TierListItemGameInfoDTO}.
     * Mapea IDs, nombre del juego, URL de la carátula y orden del ítem.
     *
     * @param tierListItem La entidad TierListItem a convertir.
     * @return El TierListItemGameInfoDTO resultante.
     */
    @Mapping(target = "tierListItemId", source = "tierListItem.internalId")
    @Mapping(target = "userGameId", source = "tierListItem.userGame.internalId")
    @Mapping(target = "gameIgdbId", source = "tierListItem.userGame.game.igdbId")
    @Mapping(target = "gameName", source = "tierListItem.userGame.game.name")
    @Mapping(target = "gameCoverUrl", source = "tierListItem.userGame.game", qualifiedByName = "gameToCoverUrlTierList")
    @Mapping(target = "itemOrder", source = "tierListItem.itemOrder")
    TierListItemGameInfoDTO toGameInfoDTO(TierListItem tierListItem);

    /**
     * Convierte una lista de entidades {@link TierListItem} a una lista de {@link TierListItemGameInfoDTO}.
     *
     * @param tierListItems La lista de entidades TierListItem a convertir.
     * @return La lista de TierListItemGameInfoDTO resultante.
     */
    List<TierListItemGameInfoDTO> toGameInfoDTOList(List<TierListItem> tierListItems);

    /**
     * Método calificado por nombre para extraer la URL de la carátula de un juego.
     * Utilizado en el mapeo de {@code gameCoverUrl}.
     *
     * @param game La entidad {@link Game} de la cual extraer la URL de la carátula.
     * @return La URL de la carátula, o {@code null} si no está disponible.
     */
    @Named("gameToCoverUrlTierList")
    default String gameToCoverUrlTierList(Game game) {
        if (game != null && game.getCover() != null) {
            return game.getCover().getUrl();
        }
        return null;
    }
}