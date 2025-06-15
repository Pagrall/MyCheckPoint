# UserProfileUpdateDTO

DTO para actualizar el perfil de un usuario. Solo los campos presentes y no nulos (o que cumplan @NotNull) serán considerados para la actualización. Para campos como \'foto_perfil\', enviar un nuevo valor actualiza, enviar null o no enviar el campo lo deja sin cambios.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**nombre_usuario** | **string** | Nuevo nombre de usuario para la cuenta. Si se proporciona, debe tener entre 3 y 100 caracteres. Si no se envía o es nulo, no se actualiza. | [optional] [default to undefined]
**tema** | **string** | Nuevo tema de la interfaz preferido por el usuario. Si no se envía o es nulo, no se actualiza. | [optional] [default to undefined]
**foto_perfil** | **string** | Nueva URL de la foto de perfil del usuario. Este campo normalmente se actualiza a través del endpoint de subida de imágenes. Si se proporciona aquí, debería ser una URL válida a una imagen ya alojada. Si no se envía o es nulo, no se actualiza. | [optional] [default to undefined]
**notificaciones** | **boolean** | Preferencia para recibir notificaciones. Si se envía, no puede ser nulo. | [default to undefined]
**visibilidad_perfil** | **string** | Nuevo nivel de visibilidad del perfil del usuario. Si no se envía o es nulo, no se actualiza. | [optional] [default to undefined]

## Example

```typescript
import { UserProfileUpdateDTO } from '@mycheckpoint/api-client';

const instance: UserProfileUpdateDTO = {
    nombre_usuario,
    tema,
    foto_perfil,
    notificaciones,
    visibilidad_perfil,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
