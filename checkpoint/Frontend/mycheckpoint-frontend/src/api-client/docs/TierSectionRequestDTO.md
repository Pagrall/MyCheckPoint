# TierSectionRequestDTO

DTO para solicitar la creación o actualización del nombre de una nueva sección (tier) dentro de una Tier List.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **string** | Nombre de la nueva sección (tier). Debe tener entre 1 y 100 caracteres. | [default to undefined]
**color** | **string** | Color de la sección (tier) en formato hexadecimal. | [default to undefined]

## Example

```typescript
import { TierSectionRequestDTO } from '@mycheckpoint/api-client';

const instance: TierSectionRequestDTO = {
    name,
    color,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
