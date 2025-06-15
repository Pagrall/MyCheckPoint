# AddGameToCustomListRequestDTO

DTO para añadir un juego existente de la biblioteca del usuario a una lista de juegos personalizada.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**user_game_id** | **number** | ID interno de la entrada \&#39;UserGame\&#39; (juego en la biblioteca del usuario) que se desea añadir a la lista. Es obligatorio. | [default to undefined]

## Example

```typescript
import { AddGameToCustomListRequestDTO } from '@mycheckpoint/api-client';

const instance: AddGameToCustomListRequestDTO = {
    user_game_id,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
