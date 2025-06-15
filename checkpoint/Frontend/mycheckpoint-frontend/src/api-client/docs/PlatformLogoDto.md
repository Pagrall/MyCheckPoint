# PlatformLogoDto

DTO para el logo de una plataforma de videojuegos.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**url** | **string** | URL de la imagen del logo de la plataforma. | [optional] [default to undefined]
**id** | **number** | ID del logo desde la fuente externa (ej. IGDB). | [optional] [default to undefined]

## Example

```typescript
import { PlatformLogoDto } from '@mycheckpoint/api-client';

const instance: PlatformLogoDto = {
    url,
    id,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
