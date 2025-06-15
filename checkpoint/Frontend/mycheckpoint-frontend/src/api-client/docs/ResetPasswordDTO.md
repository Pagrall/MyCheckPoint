# ResetPasswordDTO

DTO para la solicitud de restablecimiento de contraseña. Contiene el token recibido por email y la nueva contraseña.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**token** | **string** | El token de restablecimiento único que el usuario recibió por correo electrónico. | [default to undefined]
**nueva_contrasea** | **string** | La nueva contraseña deseada para la cuenta. Debe tener entre 8 y 100 caracteres. | [default to undefined]

## Example

```typescript
import { ResetPasswordDTO } from '@mycheckpoint/api-client';

const instance: ResetPasswordDTO = {
    token,
    nueva_contrasea,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
