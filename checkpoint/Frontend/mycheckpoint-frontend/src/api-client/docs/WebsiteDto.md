# WebsiteDto

DTO para un sitio web asociado a un juego.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**url** | **string** | URL completa del sitio web. | [optional] [default to undefined]
**id** | **number** | ID del sitio web desde la fuente externa (ej. IGDB). | [optional] [default to undefined]

## Example

```typescript
import { WebsiteDto } from '@mycheckpoint/api-client';

const instance: WebsiteDto = {
    url,
    id,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
