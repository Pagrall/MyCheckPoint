# CompanyInfoDto

DTO con información básica de una compañía (desarrolladora, editora).

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **string** | Nombre de la compañía. | [optional] [default to undefined]
**id** | **number** | ID de la compañía desde la fuente externa (ej. IGDB). | [optional] [default to undefined]

## Example

```typescript
import { CompanyInfoDto } from '@mycheckpoint/api-client';

const instance: CompanyInfoDto = {
    name,
    id,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
