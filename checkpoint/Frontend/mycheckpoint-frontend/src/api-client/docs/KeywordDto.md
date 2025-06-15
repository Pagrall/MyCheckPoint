# KeywordDto

DTO para una palabra clave asociada a un juego.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **string** | Nombre de la palabra clave. | [optional] [default to undefined]
**id** | **number** | ID de la palabra clave desde la fuente externa (ej. IGDB). | [optional] [default to undefined]

## Example

```typescript
import { KeywordDto } from '@mycheckpoint/api-client';

const instance: KeywordDto = {
    name,
    id,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
