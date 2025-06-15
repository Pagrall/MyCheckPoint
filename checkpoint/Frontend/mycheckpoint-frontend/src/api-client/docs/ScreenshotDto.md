# ScreenshotDto

DTO para una captura de pantalla de un juego.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**url** | **string** | URL de la imagen de la captura de pantalla. | [optional] [default to undefined]
**id** | **number** | ID de la captura de pantalla desde la fuente externa (ej. IGDB). | [optional] [default to undefined]

## Example

```typescript
import { ScreenshotDto } from '@mycheckpoint/api-client';

const instance: ScreenshotDto = {
    url,
    id,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
