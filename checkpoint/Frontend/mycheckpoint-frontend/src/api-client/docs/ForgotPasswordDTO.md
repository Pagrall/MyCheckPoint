# ForgotPasswordDTO

DTO para la solicitud de restablecimiento de contraseña. Requiere el correo electrónico asociado a la cuenta.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**email** | **string** | Correo electrónico del usuario que solicita el restablecimiento de contraseña. Debe ser un formato de email válido. | [default to undefined]

## Example

```typescript
import { ForgotPasswordDTO } from '@mycheckpoint/api-client';

const instance: ForgotPasswordDTO = {
    email,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
