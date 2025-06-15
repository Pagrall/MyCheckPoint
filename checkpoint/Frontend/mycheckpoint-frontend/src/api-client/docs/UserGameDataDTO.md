# UserGameDataDTO

DTO para proporcionar o actualizar los datos específicos de un usuario para un juego en su biblioteca.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**status** | **string** | Estado del juego en la biblioteca del usuario (ej. JUGANDO, COMPLETADO). | [optional] [default to undefined]
**personal_platform** | **string** | Plataforma personal en la que el usuario juega o posee el juego. | [optional] [default to undefined]
**has_possession** | **boolean** | Indica si el usuario posee físicamente o digitalmente el juego. | [optional] [default to undefined]
**score** | **number** | Puntuación personal otorgada por el usuario al juego (ej. de 0.0 a 10.0). | [optional] [default to undefined]
**comment** | **string** | Comentario público del usuario sobre el juego. Máximo 2000 caracteres. | [optional] [default to undefined]
**private_comment** | **string** | Comentario privado del usuario sobre el juego (solo visible para él). Máximo 2000 caracteres. | [optional] [default to undefined]
**start_date** | **string** | Fecha en la que el usuario comenzó a jugar el juego (formato YYYY-MM-DD). | [optional] [default to undefined]
**end_date** | **string** | Fecha en la que el usuario terminó de jugar el juego (formato YYYY-MM-DD). | [optional] [default to undefined]
**story_duration_hours** | **number** | Duración estimada en horas para completar la historia principal. | [optional] [default to undefined]
**story_secondary_duration_hours** | **number** | Duración estimada en horas para completar la historia principal y misiones secundarias importantes. | [optional] [default to undefined]
**completionist_duration_hours** | **number** | Duración estimada en horas para completar el juego al 100%. | [optional] [default to undefined]

## Example

```typescript
import { UserGameDataDTO } from '@mycheckpoint/api-client';

const instance: UserGameDataDTO = {
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
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
