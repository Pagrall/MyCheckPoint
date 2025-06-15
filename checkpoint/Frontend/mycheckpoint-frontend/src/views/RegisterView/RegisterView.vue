<template>
  <div class="register-container">
    
    <div class="register-content">
      <h2>Crear Cuenta</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="username">Nombre de Usuario:</label>
          <input type="text" id="username" v-model="username" required />
        </div>
        <div class="form-group">
          <label for="email">Email:</label>
          <input type="email" id="email" v-model="email" required />
        </div>
        <div class="form-group">
          <label for="password">Contraseña:</label>
          <input type="password" id="password" v-model="password" required />
          <small v-if="password && password.length > 0 && password.length < 8" class="password-hint">
            La contraseña debe tener al menos 8 caracteres.
          </small>
        </div>

        <button type="submit" :disabled="isLoading">
          {{ isLoading ? 'Registrando...' : 'Registrar' }}
        </button>
      </form>

      <div v-if="errorMessages.length > 0" class="error-messages">
        <h4>Error al registrar:</h4>
        <ul>
          <li v-for="(error, index) in errorMessages" :key="index">{{ error }}</li>
        </ul>
      </div>

      <div v-if="successMessage" class="success-message">
        {{ successMessage }}
      </div>
    </div>
  </div>
</template>

<script setup>
// --- 1. IMPORTACIONES Y CONFIGURACIÓN ---
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { registerUser } from '@/services/apiInstances.js';


// --- 2. ESTADO DEL COMPONENTE ---
// -- Refs para los campos del formulario --
const username = ref('');
const email = ref('');
const password = ref('');

// -- Refs para el estado de la UI --
const errorMessages = ref([]); // Un array para poder mostrar múltiples errores de validación.
const successMessage = ref('');
const isLoading = ref(false);

// Se inicializa el router, aunque no se usa para redirigir, está disponible si se necesitara.
const router = useRouter();


// --- 3. LÓGICA PRINCIPAL (MANEJADOR DEL REGISTRO) ---
/**
 * @description Maneja el envío del formulario de registro.
 * Realiza validaciones del lado del cliente, llama a la API de registro
 * y gestiona las respuestas de éxito y los diferentes tipos de error.
 */
const handleRegister = async () => {
  // Se resetean los mensajes y se activa el estado de carga al iniciar el proceso.
  errorMessages.value = [];
  successMessage.value = '';
  isLoading.value = true;

  // --- Validaciones del lado del cliente ---
  // Se realizan comprobaciones básicas antes de enviar la petición a la API
  // para evitar llamadas innecesarias y dar una respuesta más rápida al usuario.
  if (password.value.length < 8) {
    errorMessages.value = ['La contraseña debe tener al menos 8 caracteres.'];
    isLoading.value = false;
    return;
  }
  if (username.value.length < 3) {
    errorMessages.value = ['El nombre de usuario debe tener al menos 3 caracteres.'];
    isLoading.value = false;
    return;
  }

  // Se construye el objeto DTO (Data Transfer Object) con el formato
  // que espera el endpoint de la API para el registro.
  /** @type {import('@/api-client/index.js').UserCreateDTO} */
  const userCreateData = { 
    nombre_usuario: username.value, 
    email: email.value, 
    contraseña: password.value, 
  };

  // Se utiliza un bloque try/catch/finally para manejar la llamada asíncrona a la API.
  try {
    // --- Bloque de Éxito ---
    const response = await registerUser(userCreateData);
    // Si el registro es exitoso (código 201), se muestra un mensaje de éxito.
    successMessage.value = `¡Registro exitoso para ${response.data.nombre_usuario}! Se ha enviado un correo de confirmación a ${response.data.email}. Por favor, verifica tu cuenta.`;
    
    // Se limpian los campos del formulario tras el éxito.
    username.value = '';
    email.value = '';
    password.value = '';

  } catch (error) {
    // --- Bloque de Manejo de Errores ---
    // Este bloque se ejecuta si la promesa de 'registerUser' es rechazada.

    // Caso 1: El servidor respondió con un código de error (4xx, 5xx).
    if (error.response) {
      // Error 400: Error de validación de datos por parte del backend.
      if (error.response.status === 400) {
        if (error.response.data && error.response.data.errors) {
          errorMessages.value = error.response.data.errors;
        } else {
          errorMessages.value = ['Los datos proporcionados no son válidos. Por favor, revisa los campos.'];
        }
      // Error 409: Conflicto, el email o nombre de usuario ya existen.
      } else if (error.response.status === 409) {
        if (error.response.data && error.response.data.message) {
          errorMessages.value = [error.response.data.message];
        } else {
          errorMessages.value = ['El nombre de usuario o el email ya están registrados.'];
        }
      // Cualquier otro error del servidor.
      } else {
        errorMessages.value = [`Error del servidor (${error.response.status}): ${error.response.data.error || 'Ocurrió un problema.'}`];
      }
    
    // Caso 2: La petición se hizo pero no se recibió respuesta (error de red).
    } else if (error.request) {
      errorMessages.value = ['No se pudo conectar con el servidor. Por favor, inténtalo más tarde.'];
    
    // Caso 3: Error en la configuración de la petición que impidió que se enviara.
    } else {
      errorMessages.value = [`Error: ${error.message}`];
    }
  } finally {
    // --- Bloque Final ---
    // Este bloque se ejecuta siempre, independientemente del resultado (éxito o error).
    // Es el lugar idóneo para desactivar el estado de carga.
    isLoading.value = false;
  }
};
</script>

<style src="./RegisterView.css" scoped></style>