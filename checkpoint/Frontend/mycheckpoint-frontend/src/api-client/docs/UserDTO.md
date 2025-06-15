# UserDTO

DTO que representa la información pública y de preferencias de un usuario. Se devuelve tras un registro o al solicitar datos de usuario.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**public_id** | **string** | Identificador público único del usuario, generado automáticamente. | [optional] [readonly] [default to undefined]
**nombre_usuario** | **string** | Nombre de usuario elegido por el usuario. | [optional] [default to undefined]
**email** | **string** | Dirección de correo electrónico del usuario. | [optional] [default to undefined]
**fecha_registro** | **string** | Fecha y hora en que el usuario se registró en el sistema (formato ISO 8601). | [optional] [readonly] [default to undefined]
**tema** | **string** | Tema de la interfaz preferido por el usuario. | [optional] [default to undefined]
**foto_perfil** | **string** | URL relativa o absoluta de la foto de perfil del usuario. Puede ser nulo si no se ha subido ninguna. | [optional] [default to undefined]
**notificaciones** | **boolean** | Indica si el usuario desea recibir notificaciones. Por defecto es true. | [optional] [default to undefined]
**visibilidad_perfil** | **string** | Nivel de visibilidad del perfil del usuario. Por defecto es PUBLICO. | [optional] [default to undefined]

## Example

```typescript
import { UserDTO } from '@mycheckpoint/api-client';

const instance: UserDTO = {
    public_id,
    nombre_usuario,
    email,
    fecha_registro,
    tema,
    foto_perfil,
    notificaciones,
    visibilidad_perfil,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
