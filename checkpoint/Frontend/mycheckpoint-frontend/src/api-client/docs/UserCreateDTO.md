# UserCreateDTO

DTO para la creación de un nuevo usuario. Contiene los campos obligatorios para el registro.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**nombre_usuario** | **string** | Nombre de usuario único para la cuenta. Debe tener entre 3 y 100 caracteres. | [default to undefined]
**email** | **string** | Dirección de correo electrónico del usuario. Debe ser única y tener un formato válido. | [default to undefined]
**contrasea** | **string** | Contraseña para la cuenta del usuario. Debe tener entre 8 y 100 caracteres. | [default to undefined]

## Example

```typescript
import { UserCreateDTO } from '@mycheckpoint/api-client';

const instance: UserCreateDTO = {
    nombre_usuario,
    email,
    contrasea,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
