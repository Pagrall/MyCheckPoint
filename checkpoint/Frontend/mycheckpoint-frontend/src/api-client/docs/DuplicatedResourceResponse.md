# DuplicatedResourceResponse

Respuesta específica para errores de recursos duplicados (HTTP 409).

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**message** | **string** | Mensaje de error indicando que el recurso ya existe. | [optional] [default to undefined]

## Example

```typescript
import { DuplicatedResourceResponse } from '@mycheckpoint/api-client';

const instance: DuplicatedResourceResponse = {
    message,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
