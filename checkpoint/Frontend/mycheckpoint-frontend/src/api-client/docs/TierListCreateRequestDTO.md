# TierListCreateRequestDTO

DTO para crear una nueva Tier List. Se utiliza principalmente para Tier Lists de perfil.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **string** | Nombre de la Tier List. Debe tener entre 1 y 150 caracteres. | [default to undefined]
**description** | **string** | Descripción opcional para la Tier List. Máximo 1000 caracteres. | [optional] [default to undefined]
**is_public** | **boolean** | Indica si la Tier List será pública (true) o privada (false). Por defecto es false (privada). | [optional] [default to undefined]

## Example

```typescript
import { TierListCreateRequestDTO } from '@mycheckpoint/api-client';

const instance: TierListCreateRequestDTO = {
    name,
    description,
    is_public,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
