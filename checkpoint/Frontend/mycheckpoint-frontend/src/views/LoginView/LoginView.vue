<template>
  <div class="login-container">
    <h2>Iniciar Sesión</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label for="identifier">Email o Nombre de Usuario:</label>
        <input type="text" id="identifier" v-model="identifier" required />
      </div>
      <div class="form-group">
        <label for="password">Contraseña:</label>
        <input type="password" id="password" v-model="password" required />
      </div>

      <button type="submit" :disabled="isLoading">
        {{ isLoading ? 'Iniciando sesión...' : 'Iniciar Sesión' }}
      </button>
    </form>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    
    <div class="extra-links">
      <router-link to="/request-password-reset">¿Olvidaste tu contraseña?</router-link>
      <br>
      <router-link to="/register">¿No tienes cuenta? Regístrate</router-link>
    </div>
  </div>
</template>

<script setup>
// --- 1. IMPORTACIONES ---
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore.js';
import { loginUser } from '@/services/apiInstances.js';


// --- 2. CONFIGURACIÓN PRINCIPAL ---
// Se inicializa el router para poder redirigir al usuario tras el login.
const router = useRouter();
// Se obtiene una instancia del store de autenticación para manejar el estado del usuario.
const authStore = useAuthStore();


// --- 3. ESTADO DEL COMPONENTE ---
// Refs para vincular los campos del formulario de login.
const identifier = ref('');
const password = ref('');

// Refs para gestionar el estado de la UI durante el proceso de login.
const errorMessage = ref('');
const isLoading = ref(false);


// --- 4. LÓGICA PRINCIPAL ---
/**
 * @description Maneja el envío del formulario de inicio de sesión.
 * Llama a la API, gestiona las respuestas de éxito y error, y actualiza el estado de la aplicación.
 */
const handleLogin = async () => {
  // Resetea el estado antes de cada intento.
  errorMessage.value = '';
  isLoading.value = true;

  // Se crea el objeto DTO (Data Transfer Object) con los datos del formulario
  // para enviarlo a la API, tal como lo espera el backend.
  /** @type {import('@/api-client/index.js').LoginRequestDTO} */
  const loginData = { 
    identificador: identifier.value, 
    contraseña: password.value, 
  };

  // El bloque try/catch/finally asegura un manejo robusto de la llamada asíncrona.
  try {
    // --- Bloque de Éxito ---
    const response = await loginUser(loginData);
    
    // Se delega al store la lógica de guardar el token y obtener los datos del usuario.
    // Esto mantiene el componente limpio y la lógica de autenticación centralizada.
    await authStore.loginSuccess(response.data);

    // Si todo va bien, se redirige al usuario a la página principal.
    router.push('/');

  } catch (error) {
    // --- Bloque de Manejo de Errores ---
    // Este bloque se ejecuta si la promesa de 'loginUser' es rechazada.

    // Caso 1: El servidor respondió con un código de error (4xx, 5xx).
    if (error.response) {
      const status = error.response.status;
      const data = error.response.data;
      
      // Error de validación de campos.
      if (status === 400) {
        if (data && data.errors && data.errors.length > 0) {
            errorMessage.value = data.errors.join(', ');
        } else {
            errorMessage.value = 'Los datos proporcionados son incorrectos.';
        }
      // Error de credenciales incorrectas.
      } else if (status === 401) {
         errorMessage.value = data.message || 'Credenciales incorrectas. Por favor, inténtalo de nuevo.';
      // Error de acceso prohibido (cuenta no verificada, baneada, etc.).
      } else if (status === 403) {
        errorMessage.value = data.error || 'Acceso prohibido. Tu cuenta podría estar deshabilitada o necesitar verificación.';
      // Cualquier otro error del servidor.
      } else {
        errorMessage.value = `Error del servidor (${status}): ${data.error || 'Ocurrió un problema al intentar iniciar sesión.'}`;
      }
    
    // Caso 2: La petición se hizo pero no se recibió respuesta (error de red).
    } else if (error.request) {
      errorMessage.value = 'No se pudo conectar con el servidor. Por favor, inténtalo más tarde.';
    
    // Caso 3: Error en la configuración de la petición antes de enviarla.
    } else {
      errorMessage.value = `Error: ${error.message}`;
    }
  } finally {
    // --- Bloque Final ---
    // Este bloque se ejecuta siempre, tanto si hubo éxito como si hubo error.
    // Es el lugar perfecto para desactivar el estado de carga y rehabilitar el botón.
    isLoading.value = false;
  }
};
</script>

<style src="./LoginView.css" scoped></style>