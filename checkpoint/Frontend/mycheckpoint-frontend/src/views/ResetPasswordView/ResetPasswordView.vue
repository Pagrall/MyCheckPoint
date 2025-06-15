<template>
  <div class="reset-password-container">
    <h2>Establecer Nueva Contraseña</h2>
    <form @submit.prevent="handleResetPassword" v-if="token">
      <div class="form-group">
        <label for="newPassword">Nueva Contraseña:</label>
        <input type="password" id="newPassword" v-model="newPassword" required />
         <small v-if="newPassword && newPassword.length > 0 && newPassword.length < 8" class="password-hint">
          La contraseña debe tener al menos 8 caracteres.
        </small>
      </div>
      <div class="form-group">
        <label for="confirmPassword">Confirmar Nueva Contraseña:</label>
        <input type="password" id="confirmPassword" v-model="confirmPassword" required />
      </div>

      <div v-if="passwordMismatch" class="error-message-inline">
        Las contraseñas no coinciden.
      </div>

      <button type="submit" :disabled="isLoading || passwordMismatch || newPassword.length < 8">
        {{ isLoading ? 'Guardando...' : 'Guardar Nueva Contraseña' }}
      </button>
    </form>
    <div v-if="!token && !message" class="error-message">
      Token de restablecimiento no encontrado o inválido.
    </div>
    <div v-if="message" :class="isError ? 'error-message' : 'success-message'">
      {{ message }}
    </div>
     <div v-if="!isError && message" class="extra-links">
      <router-link to="/login">Ir a Iniciar Sesión</router-link>
    </div>
  </div>
</template>

<script setup>
// --- 1. IMPORTACIONES Y CONFIGURACIÓN ---
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { resetPasswordWithToken } from '@/services/apiInstances.js';


// --- 2. CONFIGURACIÓN DE ROUTER ---
// Se utiliza 'useRoute' para acceder a los parámetros de la URL actual (como el token).
const route = useRoute();
// Se utiliza 'useRouter' para poder redirigir al usuario programáticamente.
const router = useRouter();


// --- 3. ESTADO DEL COMPONENTE ---
// -- Estado del formulario y del token --
const token = ref(null); // Almacenará el token de restablecimiento obtenido de la URL.
const newPassword = ref('');
const confirmPassword = ref('');

// -- Estado de la UI --
const message = ref('');    // Mensaje de feedback para el usuario (éxito o error).
const isError = ref(false); // Determina si el mensaje es de error para aplicar estilos.
const isLoading = ref(false); // Controla el estado de carga del botón.


// --- 4. PROPIEDADES COMPUTADAS ---
/**
 * @description Propiedad computada que comprueba en tiempo real si las contraseñas no coinciden.
 * Es útil para deshabilitar el botón de envío y mostrar una advertencia inmediata al usuario.
 * @returns {boolean} - True si ambas contraseñas están escritas y no son iguales.
 */
const passwordMismatch = computed(() => {
  return newPassword.value && confirmPassword.value && newPassword.value !== confirmPassword.value;
});


// --- 5. CICLO DE VIDA ---
/**
 * @description Se ejecuta cuando el componente se monta en el DOM.
 * Su función principal aquí es extraer el token de la URL.
 */
onMounted(() => {
  // Se intenta obtener el token del parámetro 'token' en la query string de la URL.
  token.value = route.query.token || null;
  // Si no se encuentra un token, el proceso no puede continuar. Se muestra un error.
  if (!token.value) {
    message.value = 'El enlace de restablecimiento es inválido o ha expirado. Por favor, solicita uno nuevo.';
    isError.value = true;
  }
});


// --- 6. LÓGICA PRINCIPAL ---
/**
 * @description Maneja el envío del formulario para establecer la nueva contraseña.
 * Realiza validaciones, llama a la API y gestiona la respuesta.
 */
const handleResetPassword = async () => {
  // Se resetean los mensajes y se activa el estado de carga.
  message.value = '';
  isError.value = false;
  isLoading.value = true;

  // --- Validaciones del lado del cliente ---
  // Se comprueban las condiciones básicas antes de hacer la llamada a la API.
  if (newPassword.value !== confirmPassword.value) {
    message.value = 'Las contraseñas no coinciden.';
    isError.value = true;
    isLoading.value = false;
    return;
  }

  if (newPassword.value.length < 8) {
    message.value = 'La nueva contraseña debe tener al menos 8 caracteres.';
    isError.value = true;
    isLoading.value = false;
    return;
  }

  // Se crea el DTO (Data Transfer Object) con el token y la nueva contraseña para la API.
  /** @type {import('@/api-client/index.js').ResetPasswordDTO} */
  const resetData = {
    token: token.value,
    nueva_contraseña: newPassword.value,
  };

  try {
    // --- Bloque de Éxito ---
    await resetPasswordWithToken(resetData);
    message.value = '¡Tu contraseña ha sido restablecida exitosamente! Ahora puedes iniciar sesión con tu nueva contraseña.';
    isError.value = false;
    // Se redirige al usuario a la página de login tras 3 segundos para que lea el mensaje.
    setTimeout(() => {
      router.push('/login');
    }, 3000);

  } catch (error) {
    // --- Bloque de Manejo de Errores ---
    // Caso 1: El servidor respondió con un error.
    if (error.response && error.response.data) {
       // Los errores 400 o 404 suelen indicar que el token es inválido/expirado
       // o que la contraseña no cumple con las políticas del backend.
      if (error.response.status === 400 || error.response.status === 404) {
        if (error.response.data.errors && Array.isArray(error.response.data.errors)) {
          message.value = error.response.data.errors.join(' ');
        } else {
          message.value = error.response.data.error || error.response.data.message || 'El token es inválido, ha expirado o la nueva contraseña no cumple los requisitos.';
        }
      } else {
        message.value = error.response.data.error || 'Ocurrió un error al restablecer tu contraseña.';
      }
    // Caso 2: Error de red (no se pudo conectar con el servidor).
    } else if (error.request) {
      message.value = 'No se pudo conectar con el servidor. Inténtalo más tarde.';
    // Caso 3: Otro tipo de error.
    } else {
      message.value = 'Ocurrió un error inesperado.';
    }
    isError.value = true;
    console.error("Error al restablecer contraseña:", error);

  } finally {
    // --- Bloque Final ---
    // Se ejecuta siempre para asegurar que el estado de carga se desactive.
    isLoading.value = false;
  }
};
</script>

<style src="./ResetPasswordView.css" scoped></style>