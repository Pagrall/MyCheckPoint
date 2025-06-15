# ArtworkDto

DTO para una obra de arte (artwork) de un juego.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**url** | **string** | URL de la imagen de la obra de arte. | [optional] [default to undefined]
**id** | **number** | ID de la obra de arte desde la fuente externa (ej. IGDB). | [optional] [default to undefined]

## Example

```typescript
import { ArtworkDto } from '@mycheckpoint/api-client';

const instance: ArtworkDto = {
    url,
    id,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
