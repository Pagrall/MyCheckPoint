# VideoDto

DTO para un vídeo asociado a un juego.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **string** | Nombre o título del vídeo. | [optional] [default to undefined]
**id** | **number** | ID del vídeo desde la fuente externa (ej. IGDB). | [optional] [default to undefined]
**video_id** | **string** | Identificador del vídeo en la plataforma de origen (ej. ID de YouTube). | [optional] [default to undefined]

## Example

```typescript
import { VideoDto } from '@mycheckpoint/api-client';

const instance: VideoDto = {
    name,
    id,
    video_id,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
