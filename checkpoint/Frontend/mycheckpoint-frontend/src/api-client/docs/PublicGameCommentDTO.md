# PublicGameCommentDTO

DTO que representa un comentario público sobre un juego.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**username** | **string** | Nombre de usuario del autor del comentario. | [optional] [default to undefined]
**user_public_id** | **string** | ID público del usuario autor del comentario. | [optional] [default to undefined]
**comment_text** | **string** | Texto del comentario. | [optional] [default to undefined]
**comment_date** | **string** | Fecha y hora de la última actualización del comentario (proveniente de UserGame.updatedAt). | [optional] [default to undefined]

## Example

```typescript
import { PublicGameCommentDTO } from '@mycheckpoint/api-client';

const instance: PublicGameCommentDTO = {
    username,
    user_public_id,
    comment_text,
    comment_date,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
