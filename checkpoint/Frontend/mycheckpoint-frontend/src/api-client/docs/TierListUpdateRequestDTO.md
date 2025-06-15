# TierListUpdateRequestDTO

DTO para actualizar los metadatos de una Tier List existente (nombre, descripción, visibilidad). Solo los campos proporcionados (no nulos) serán actualizados.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **string** | Nuevo nombre para la Tier List. Si se proporciona, debe tener entre 1 y 150 caracteres. | [optional] [default to undefined]
**description** | **string** | Nueva descripción para la Tier List. Si se proporciona, no puede exceder los 1000 caracteres. | [optional] [default to undefined]
**is_public** | **boolean** | Nuevo estado de visibilidad para la Tier List (true para pública, false para privada). Si se proporciona, se actualizará el estado. | [optional] [default to undefined]

## Example

```typescript
import { TierListUpdateRequestDTO } from '@mycheckpoint/api-client';

const instance: TierListUpdateRequestDTO = {
    name,
    description,
    is_public,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
