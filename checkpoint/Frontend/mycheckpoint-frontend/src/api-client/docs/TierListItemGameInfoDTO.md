# TierListItemGameInfoDTO

DTO con información de un juego para un ítem de Tier List.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**tier_list_item_id** | **number** | ID interno del ítem en la Tier List. | [optional] [default to undefined]
**user_game_id** | **number** | ID interno de la entrada UserGame asociada. | [optional] [default to undefined]
**game_igdb_id** | **number** | ID de IGDB del juego. | [optional] [default to undefined]
**game_name** | **string** | Nombre del juego. | [optional] [default to undefined]
**game_cover_url** | **string** | URL de la carátula del juego. | [optional] [default to undefined]
**item_order** | **number** | Orden del ítem dentro de su sección. | [optional] [default to undefined]

## Example

```typescript
import { TierListItemGameInfoDTO } from '@mycheckpoint/api-client';

const instance: TierListItemGameInfoDTO = {
    tier_list_item_id,
    user_game_id,
    game_igdb_id,
    game_name,
    game_cover_url,
    item_order,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
