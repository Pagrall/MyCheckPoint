# UserGameResponseDTO

DTO que representa una entrada en la biblioteca de juegos de un usuario, incluyendo sus datos personales sobre un juego específico.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**internal_id** | **number** |  | [optional] [default to undefined]
**game_igdb_id** | **number** | ID de IGDB del juego al que se refiere esta entrada de la biblioteca. | [optional] [default to undefined]
**game_name** | **string** | Nombre del juego. | [optional] [default to undefined]
**game_cover** | [**CoverDto**](CoverDto.md) |  | [optional] [default to undefined]
**status** | **string** | Estado actual del juego en la biblioteca del usuario. | [optional] [default to undefined]
**personal_platform** | **string** | Plataforma personal en la que el usuario juega o posee el juego. | [optional] [default to undefined]
**has_possession** | **boolean** | Indica si el usuario posee el juego. | [optional] [default to undefined]
**score** | **number** | Puntuación personal otorgada por el usuario al juego. | [optional] [default to undefined]
**comment** | **string** | Comentario público del usuario sobre el juego. | [optional] [default to undefined]
**private_comment** | **string** | Comentario privado del usuario sobre el juego (no visible para otros). | [optional] [default to undefined]
**start_date** | **string** | Fecha en que el usuario comenzó a jugar (YYYY-MM-DD). | [optional] [default to undefined]
**end_date** | **string** | Fecha en que el usuario terminó de jugar (YYYY-MM-DD). | [optional] [default to undefined]
**story_duration_hours** | **number** | Horas dedicadas a la historia principal. | [optional] [default to undefined]
**story_secondary_duration_hours** | **number** | Horas dedicadas a la historia principal y secundarias. | [optional] [default to undefined]
**completionist_duration_hours** | **number** | Horas dedicadas para completar el juego al 100%. | [optional] [default to undefined]
**created_at** | **string** | Fecha y hora de creación de esta entrada en la biblioteca. | [optional] [readonly] [default to undefined]
**updated_at** | **string** | Fecha y hora de la última actualización de esta entrada. | [optional] [readonly] [default to undefined]

## Example

```typescript
import { UserGameResponseDTO } from '@mycheckpoint/api-client';

const instance: UserGameResponseDTO = {
    internal_id,
    game_igdb_id,
    game_name,
    game_cover,
    status,
    personal_platform,
    has_possession,
    score,
    comment,
    private_comment,
    start_date,
    end_date,
    story_duration_hours,
    story_secondary_duration_hours,
    completionist_duration_hours,
    created_at,
    updated_at,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
