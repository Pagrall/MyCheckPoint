# GameDetailDTO

DTO que contiene los detalles completos de un juego, incluyendo información general, datos específicos del usuario (si está autenticado y el juego está en su biblioteca), y comentarios públicos.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**game_info** | [**GameDto**](GameDto.md) |  | [optional] [default to undefined]
**user_game_data** | [**UserGameResponseDTO**](UserGameResponseDTO.md) |  | [optional] [default to undefined]
**public_comments** | [**Array&lt;PublicGameCommentDTO&gt;**](PublicGameCommentDTO.md) | Lista de comentarios públicos realizados por otros usuarios sobre este juego. Puede estar vacía si no hay comentarios o si el juego no existe en la base de datos local para asociar comentarios. | [optional] [default to undefined]

## Example

```typescript
import { GameDetailDTO } from '@mycheckpoint/api-client';

const instance: GameDetailDTO = {
    game_info,
    user_game_data,
    public_comments,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
