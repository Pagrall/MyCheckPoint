# RequiredErrorResponse

Respuesta específica para errores de validación de campos obligatorios (HTTP 400).

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**errors** | **Array&lt;string&gt;** | Mensaje de error genérico para campos obligatorios. | [optional] [default to undefined]

## Example

```typescript
import { RequiredErrorResponse } from '@mycheckpoint/api-client';

const instance: RequiredErrorResponse = {
    errors,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
