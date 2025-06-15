# PlatformDto

DTO para una plataforma de videojuegos.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **string** | Nombre principal de la plataforma. | [optional] [default to undefined]
**id** | **number** | ID de la plataforma desde IGDB. | [optional] [default to undefined]
**alternative_name** | **string** | Nombre alternativo de la plataforma. | [optional] [default to undefined]
**platform_logo** | [**PlatformLogoDto**](PlatformLogoDto.md) |  | [optional] [default to undefined]

## Example

```typescript
import { PlatformDto } from '@mycheckpoint/api-client';

const instance: PlatformDto = {
    name,
    id,
    alternative_name,
    platform_logo,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
