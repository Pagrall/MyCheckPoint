# GameListResponseDTO

DTO que representa una lista de juegos personalizada, incluyendo sus detalles y los juegos que contiene.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**public_id** | **string** | ID público único de la lista de juegos. | [optional] [readonly] [default to undefined]
**name** | **string** | Nombre de la lista de juegos. | [optional] [default to undefined]
**description** | **string** | Descripción de la lista de juegos. | [optional] [default to undefined]
**is_public** | **boolean** | Indica si la lista de juegos es pública (true) o privada (false). | [optional] [default to undefined]
**owner_username** | **string** | Nombre de usuario del propietario de la lista. | [optional] [readonly] [default to undefined]
**games_in_list** | [**Array&lt;UserGameResponseDTO&gt;**](UserGameResponseDTO.md) | Lista de juegos (entradas de la biblioteca del usuario) incluidos en esta lista. Puede estar vacía. | [optional] [default to undefined]
**game_count** | **number** | Número total de juegos en la lista. | [optional] [readonly] [default to undefined]
**created_at** | **string** | Fecha y hora de creación de la lista (formato ISO 8601). | [optional] [readonly] [default to undefined]
**updated_at** | **string** | Fecha y hora de la última actualización de la lista (formato ISO 8601). | [optional] [readonly] [default to undefined]

## Example

```typescript
import { GameListResponseDTO } from '@mycheckpoint/api-client';

const instance: GameListResponseDTO = {
    public_id,
    name,
    description,
    is_public,
    owner_username,
    games_in_list,
    game_count,
    created_at,
    updated_at,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
