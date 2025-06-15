<template>
  <div class="user-settings-container">
    <h2>Ajustes del Perfil</h2>

    <div class="settings-layout-wrapper">
      
      <div class="settings-column-left">
        <section class="settings-section">
          <h3>Foto de Perfil</h3>
          <div class="profile-picture-area">
            <p>Foto Actual:</p>
            <img :src="currentProfilePictureUrl" alt="Foto de perfil actual" class="profile-picture-current" />
            <div class="form-group">
              <label for="profilePictureInput">Seleccionar nueva foto:</label>
              <input type="file" id="profilePictureInput" @change="onFileSelected" accept="image/png, image/jpeg, image/gif" />
            </div>
            <div v-if="imagePreviewUrl" class="image-preview-container">
              <p>Vista Previa:</p>
              <img :src="imagePreviewUrl" alt="Vista previa de nueva foto" class="profile-picture-preview" />
            </div>
            <button @click="handleUploadPhoto" :disabled="!selectedFile || isLoadingPhoto" class="upload-button">
              {{ isLoadingPhoto ? 'Subiendo...' : 'Subir Nueva Foto' }}
            </button>
            <div v-if="photoMessage" :class="photoError ? 'error-message' : 'success-message'">
              {{ photoMessage }}
            </div>
          </div>
        </section>
      </div>
      <div class="settings-column-right">
        <section class="settings-section">
          <h3>Actualizar Información del Perfil</h3>
          <form @submit.prevent="handleUpdateProfile">
            <div class="form-group">
              <label for="username">Nombre de Usuario:</label>
              <input type="text" id="username" v-model="profileForm.nombre_usuario" />
              <small v-if="profileForm.nombre_usuario && profileForm.nombre_usuario.length > 0 && profileForm.nombre_usuario.length < 3" class="input-hint">
                Debe tener al menos 3 caracteres.
              </small>
            </div>
            <!--
            <div class="form-group">
              <label for="theme">Tema Preferido:</label>
              <select id="theme" v-model="profileForm.tema">
                <option value="CLARO">Claro</option>
                <option value="OSCURO">Oscuro</option>
              </select>
            </div>
            -->
            <!--
            <div class="form-group">
              <label for="notifications">Recibir Notificaciones:</label>
              <input type="checkbox" id="notifications" v-model="profileForm.notificaciones" />
            </div>
            -->
            <div class="form-group">
              <label for="profileVisibility">Visibilidad del Perfil:</label>
              <select id="profileVisibility" v-model="profileForm.visibilidad_perfil">
                <option value="PUBLICO">Público</option>
                <option value="PRIVADO">Privado</option>
                <option value="SOLO_AMIGOS">Solo Amigos</option>
              </select>
            </div>
            <button type="submit" :disabled="isLoadingProfile">
              {{ isLoadingProfile ? 'Guardando...' : 'Guardar Cambios de Perfil' }}
            </button>
            <div v-if="profileMessage" :class="profileError ? 'error-message' : 'success-message'">
              {{ profileMessage }}
            </div>
          </form>
        </section>
    
        <section class="settings-section">
          <h3>Cambiar Contraseña</h3>
          <form @submit.prevent="handleChangePassword">
             <div class="form-group">
              <label for="currentPassword">Contraseña Actual:</label>
              <input type="password" id="currentPassword" v-model="passwordChangeForm.currentPassword" required />
            </div>
            <div class="form-group">
              <label for="newPasswordForChange">Nueva Contraseña:</label>
              <input type="password" id="newPasswordForChange" v-model="passwordChangeForm.newPassword" required />
              <small v-if="passwordChangeForm.newPassword && passwordChangeForm.newPassword.length > 0 && passwordChangeForm.newPassword.length < 8" class="input-hint">
                Debe tener al menos 8 caracteres.
              </small>
            </div>
            <div class="form-group">
              <label for="confirmNewPassword">Confirmar Nueva Contraseña:</label>
              <input type="password" id="confirmNewPassword" v-model="passwordChangeForm.confirmNewPassword" required />
            </div>
            <div v-if="passwordChangeForm.newPassword && passwordChangeForm.confirmNewPassword && passwordChangeForm.newPassword !== passwordChangeForm.confirmNewPassword" class="error-message-inline">
              Las nuevas contraseñas no coinciden.
            </div>
            <button type="submit" :disabled="isLoadingChangePassword || passwordChangeForm.newPassword !== passwordChangeForm.confirmNewPassword || passwordChangeForm.newPassword.length < 8">
              {{ isLoadingChangePassword ? 'Cambiando...' : 'Cambiar Contraseña' }}
            </button>
            <div v-if="changePasswordMessage" :class="changePasswordError ? 'error-message' : 'success-message'">
              {{ changePasswordMessage }}
            </div>
          </form>
        </section>
    
        <section class="settings-section delete-account-section">
          <h3>Eliminar Cuenta</h3>
          <p>
            Esta acción programará la eliminación permanente de tu cuenta después de un período de gracia.
            Perderás acceso a todos tus datos. 
            <br>
            Para deshacer esta acción, deberas volver a iniciar sesion antes de que se complete el tiempo de gracia.
          </p>
          <button @click="requestAccountDeletion" v-if="!showDeleteConfirmation" class="delete-button-request">
            Solicitar Eliminación de Cuenta
          </button>
    
          <div v-if="showDeleteConfirmation" class="delete-confirmation-area">
            <p>Para confirmar la eliminación de tu cuenta, por favor ingresa tu contraseña actual.</p>
            <div class="form-group">
              <label for="passwordForDelete">Contraseña Actual:</label>
              <input type="password" id="passwordForDelete" v-model="passwordForDelete" required />
            </div>
            <button @click="handleDeleteAccount" :disabled="isLoadingDeleteAccount || !passwordForDelete" class="delete-button-confirm">
              {{ isLoadingDeleteAccount ? 'Eliminando...' : 'Eliminar Mi Cuenta Permanentemente' }}
            </button>
          </div>
          <div v-if="deleteAccountMessage" :class="deleteAccountError ? 'error-message' : 'success-message'">
            {{ deleteAccountMessage }}
          </div>
        </section>
      </div>
      </div>
    </div>
</template>

<script setup>
// --- 1. IMPORTACIONES Y CONFIGURACIÓN ---
import { ref, onMounted, reactive, computed, watch } from 'vue';
import { useAuthStore } from '@/stores/authStore.js';
import { useRouter } from 'vue-router';
import {
  updateUserProfile,
  getCurrentAuthenticatedUser,
  uploadUserProfilePicture,
  changeUserPassword,
  deleteUserAccount
} from '@/services/apiInstances.js';
import { BASE_PATH } from '@/api-client/base.js';
import defaultAvatar from '@/assets/img/default-avatar.svg';

const authStore = useAuthStore();
const router = useRouter();


// --- 2. ESTADO DEL COMPONENTE ---

// -- Estado para Actualizar Información del Perfil --
const profileForm = reactive({
  nombre_usuario: '',
  tema: 'CLARO',
  notificaciones: true,
  visibilidad_perfil: 'PUBLICO',
});
const isLoadingProfile = ref(false);
const profileMessage = ref('');
const profileError = ref(false);

// -- Estado para Foto de Perfil --
const selectedFile = ref(null); // Almacena el archivo de imagen seleccionado.
const imagePreviewUrl = ref(null); // URL para la vista previa de la imagen.
const isLoadingPhoto = ref(false);
const photoMessage = ref('');
const photoError = ref(false);

// -- Estado para Cambiar Contraseña --
const passwordChangeForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmNewPassword: '',
});
const isLoadingChangePassword = ref(false);
const changePasswordMessage = ref('');
const changePasswordError = ref(false);

// -- Estado para Eliminar Cuenta --
const showDeleteConfirmation = ref(false); // Controla si se muestra el campo de contraseña para confirmar.
const passwordForDelete = ref('');
const isLoadingDeleteAccount = ref(false);
const deleteAccountMessage = ref('');
const deleteAccountError = ref(false);


// --- 3. PROPIEDADES COMPUTADAS ---
/**
 * @description Construye la URL de la foto de perfil actual.
 * Incluye un 'cache buster' (un parámetro de tiempo) para forzar al navegador a
 * recargar la imagen después de una actualización, en lugar de usar la versión en caché.
 */
const currentProfilePictureUrl = computed(() => {
  const fotoPerfilValue = authStore.currentUser?.foto_perfil;
  if (fotoPerfilValue) {
    let finalUrl = '';
    if (fotoPerfilValue.startsWith('http://') || fotoPerfilValue.startsWith('https://')) {
      finalUrl = fotoPerfilValue;
    } else {
      const baseApiUrl = BASE_PATH.endsWith('/') ? BASE_PATH.slice(0, -1) : BASE_PATH;
      const relativeImagePath = fotoPerfilValue.startsWith('/') ? fotoPerfilValue.substring(1) : fotoPerfilValue;
      finalUrl = `${baseApiUrl}/profile-pictures/${relativeImagePath}`;
    }
    // El 'cache buster' es crucial para que la imagen se actualice visualmente de inmediato.
    const cacheBuster = `?t=${authStore.imageUpdateTrigger}`;
    return `${finalUrl}${cacheBuster}`;
  }
  return defaultAvatar;
});


// --- 4. WATCHERS (OBSERVADORES) ---
// Estos watchers son útiles para depuración durante el desarrollo.
watch(currentProfilePictureUrl, (newValue, oldValue) => {
  console.log(`DEBUG: currentProfilePictureUrl cambió de: ${oldValue} a: ${newValue}`);
});
watch(() => authStore.currentUser?.foto_perfil, (newVal, oldVal) => {
    console.log(`DEBUG: authStore.currentUser.foto_perfil cambió de ${oldVal} a ${newVal}`);
});


// --- 5. CICLO DE VIDA ---
/**
 * @description Se ejecuta al montar el componente. Su objetivo es cargar los datos
 * del usuario y rellenar los formularios con la información actual.
 */
onMounted(async () => {
  if (authStore.isAuthenticated) {
    // Si los datos del usuario ya están en el store (navegación normal), se usan directamente.
    if (authStore.currentUser) {
      populateProfileForm(authStore.currentUser);
    } else {
      // Si no están (ej. al recargar la página), se intentan obtener de la API usando el token.
      console.log("UserSettingsView: Intentando cargar datos del usuario en onMounted.");
      isLoadingProfile.value = true;
      try {
        const response = await getCurrentAuthenticatedUser();
        // Se actualiza el store y el localStorage con los datos frescos.
        authStore.user = response.data; 
        localStorage.setItem('user', JSON.stringify(response.data));
        populateProfileForm(response.data);
      } catch (error) {
        profileMessage.value = 'Error al cargar los datos iniciales del perfil.';
        profileError.value = true;
        console.error("Error cargando datos del usuario para ajustes en onMounted:", error);
      } finally {
        isLoadingProfile.value = false;
      }
    }
  }
});


// --- 6. MÉTODOS ---

// -- Métodos de Gestión del Perfil --
/**
 * @description Rellena el formulario de perfil con los datos del usuario.
 */
const populateProfileForm = (userData) => {
  if (!userData) return;
  profileForm.nombre_usuario = userData.nombre_usuario || '';
  profileForm.tema = userData.tema || 'CLARO';
  profileForm.notificaciones = userData.notificaciones === undefined ? true : userData.notificaciones;
  profileForm.visibilidad_perfil = userData.visibilidad_perfil || 'PUBLICO';
};

/**
 * @description Maneja el envío del formulario para actualizar la información del perfil.
 * Compara los datos del formulario con los actuales para enviar solo los campos que han cambiado.
 */
const handleUpdateProfile = async () => {
  profileMessage.value = '';
  profileError.value = false;
  isLoadingProfile.value = true;

  if (profileForm.nombre_usuario && profileForm.nombre_usuario.length < 3) {
    profileMessage.value = 'El nombre de usuario debe tener al menos 3 caracteres.';
    profileError.value = true;
    isLoadingProfile.value = false;
    return;
  }
  
  // Se construye un DTO solo con los campos que han cambiado para optimizar la petición.
  const DTOData = {};
  let changesMade = false;
  if (profileForm.nombre_usuario !== authStore.currentUser?.nombre_usuario) { DTOData.nombre_usuario = profileForm.nombre_usuario; changesMade = true; }
  if (profileForm.tema !== authStore.currentUser?.tema) { DTOData.tema = profileForm.tema; changesMade = true; }
  DTOData.notificaciones = profileForm.notificaciones;
  if (profileForm.notificaciones !== authStore.currentUser?.notificaciones) { changesMade = true; }
  if (profileForm.visibilidad_perfil !== authStore.currentUser?.visibilidad_perfil) { DTOData.visibilidad_perfil = profileForm.visibilidad_perfil; changesMade = true; }
  
  if (!changesMade) {
     profileMessage.value = 'No hay cambios para guardar.';
     isLoadingProfile.value = false;
     return;
  }
  
  try {
    const response = await updateUserProfile(DTOData);
    authStore.user = response.data; // Actualiza el store con la respuesta completa.
    localStorage.setItem('user', JSON.stringify(response.data));
    populateProfileForm(authStore.currentUser);
    profileMessage.value = 'Perfil actualizado correctamente.';
    profileError.value = false;
  } catch (error) {
    profileError.value = true;
    if (error.response) {
        // Manejo de errores específicos de la API (ej. 409 Conflicto por nombre de usuario ya existente).
    } else {
      profileMessage.value = 'Error de red al actualizar el perfil.';
    }
  } finally {
    isLoadingProfile.value = false;
  }
};

// -- Métodos para la Foto de Perfil --
/**
 * @description Se activa cuando el usuario selecciona un archivo. Valida el tipo y crea una vista previa.
 */
const onFileSelected = (event) => {
  photoMessage.value = '';
  photoError.value = false;
  const file = event.target.files[0];
  if (file) {
    const allowedTypes = ['image/png', 'image/jpeg', 'image/gif'];
    if (!allowedTypes.includes(file.type)) {
      photoMessage.value = 'Tipo de archivo no permitido. Sube PNG, JPEG o GIF.';
      photoError.value = true;
      selectedFile.value = null;
      imagePreviewUrl.value = null;
      if(event.target) event.target.value = '';
      return;
    }
    selectedFile.value = file;
    // URL.createObjectURL crea una URL local para mostrar la imagen antes de subirla.
    imagePreviewUrl.value = URL.createObjectURL(file);
  } else {
    selectedFile.value = null;
    imagePreviewUrl.value = null;
  }
};

/**
 * @description Maneja la subida del archivo de imagen seleccionado a la API.
 */
const handleUploadPhoto = async () => {
 if (!selectedFile.value) {
    photoMessage.value = 'Por favor, selecciona un archivo primero.';
    photoError.value = true;
    return;
  }
  photoMessage.value = '';
  photoError.value = false;
  isLoadingPhoto.value = true;
  try {
    const response = await uploadUserProfilePicture(selectedFile.value);
    // Se usa una acción del store para actualizar los datos del usuario, lo que a su vez
    // actualiza el 'imageUpdateTrigger' y fuerza la recarga de la imagen.
    authStore.updateUser(response.data);
    localStorage.setItem('user', JSON.stringify(response.data));
    photoMessage.value = 'Foto de perfil actualizada correctamente.';
    photoError.value = false;
    selectedFile.value = null;
    imagePreviewUrl.value = null;
    if (document.getElementById('profilePictureInput')) document.getElementById('profilePictureInput').value = '';
  } catch (error) {
    photoError.value = true;
    if (error.response) {
      // Manejo de errores de la API (ej. 413 Payload Too Large).
    } else {
      photoMessage.value = 'Error de red al subir la foto.';
    }
  } finally {
    isLoadingPhoto.value = false;
  }
};

// -- Método para Cambiar la Contraseña --
const handleChangePassword = async () => {
  changePasswordMessage.value = '';
  changePasswordError.value = false;

  if (passwordChangeForm.newPassword !== passwordChangeForm.confirmNewPassword) {
    changePasswordMessage.value = 'La nueva contraseña y su confirmación no coinciden.';
    changePasswordError.value = true;
    return;
  }
  if (passwordChangeForm.newPassword.length < 8) {
    changePasswordMessage.value = 'La nueva contraseña debe tener al menos 8 caracteres.';
    changePasswordError.value = true;
    return;
  }
   if (!passwordChangeForm.currentPassword) {
    changePasswordMessage.value = 'Por favor, introduce tu contraseña actual.';
    changePasswordError.value = true;
    return;
  }

  isLoadingChangePassword.value = true;

  const passwordData = {
    contraseña_actual: passwordChangeForm.currentPassword,
    nueva_contraseña: passwordChangeForm.newPassword,
  };

  try {
    await changeUserPassword(passwordData);
    changePasswordMessage.value = 'Contraseña actualizada correctamente.';
    changePasswordError.value = false;
    
    // Se limpian los campos del formulario por seguridad.
    passwordChangeForm.currentPassword = '';
    passwordChangeForm.newPassword = '';
    // LÍNEA CORREGIDA:
    passwordChangeForm.confirmNewPassword = '';

  } catch (error) {
    changePasswordError.value = true;
    if (error.response) {
      console.error("Error cambiando contraseña:", error.response.data);
      const status = error.response.status;
      const data = error.response.data;
      if (status === 400) {
        if (data && data.errors && data.errors.length > 0) {
           changePasswordMessage.value = data.errors.join(', ');
        } else {
          changePasswordMessage.value = data.error || data.message || 'Error en los datos proporcionados o la nueva contraseña es igual a la actual.';
        }
      } else if (status === 401) {
        changePasswordMessage.value = data.message || 'La contraseña actual es incorrecta.';
      } else {
        changePasswordMessage.value = data.error || data.message || 'Error al cambiar la contraseña.';
      }
    } else {
      changePasswordMessage.value = 'Error de red al cambiar la contraseña.';
    }
  } finally {
    isLoadingChangePassword.value = false;
  }
};

// -- Métodos para Eliminar la Cuenta --
/**
 * @description Muestra el campo de confirmación por contraseña.
 */
const requestAccountDeletion = () => {
  deleteAccountMessage.value = '';
  deleteAccountError.value = false;
  showDeleteConfirmation.value = true;
};

/**
 * @description Realiza la llamada a la API para eliminar la cuenta tras confirmar con la contraseña.
 */
const handleDeleteAccount = async () => {
  if (!passwordForDelete.value) {
    deleteAccountMessage.value = 'Por favor, ingresa tu contraseña actual para confirmar.';
    deleteAccountError.value = true;
    return;
  }
  deleteAccountMessage.value = '';
  deleteAccountError.value = false;
  isLoadingDeleteAccount.value = true;
  const deleteData = {
    contraseña_actual: passwordForDelete.value,
  };
  try {
    await deleteUserAccount(deleteData);
    deleteAccountMessage.value = 'Tu cuenta ha sido programada para eliminación. Serás desconectado.';
    deleteAccountError.value = false;
    // Tras la petición exitosa, se desloguea al usuario y se le redirige al login.
    setTimeout(() => {
      authStore.logout();
      router.push('/login');
    }, 3000);
  } catch (error) {
    deleteAccountError.value = true;
    if (error.response) {
      // Manejo de errores (ej. 401 si la contraseña es incorrecta).
    } else {
      deleteAccountMessage.value = 'Error de red al intentar eliminar la cuenta.';
    }
  } finally {
    isLoadingDeleteAccount.value = false;
  }
};
</script>

<style src="./UserSettingsView.css" scoped></style>