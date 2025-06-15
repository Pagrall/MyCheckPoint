<template>
  <div class="request-reset-container">
    <h2>Restablecer Contraseña</h2>
    <p>Ingresa tu dirección de correo electrónico y te enviaremos un enlace para restablecer tu contraseña.</p>
    <form @submit.prevent="handleRequestReset">
      <div class="form-group">
        <label for="email">Email:</label>
        <input type="email" id="email" v-model="email" required />
      </div>

      <button type="submit" :disabled="isLoading">
        {{ isLoading ? 'Enviando...' : 'Enviar Enlace de Restablecimiento' }}
      </button>
    </form>

    <div v-if="message" :class="isError ? 'error-message' : 'success-message'">
      {{ message }}
    </div>
    <div class="extra-links">
      <router-link to="/login">Volver a Iniciar Sesión</router-link>
    </div>
  </div>
</template>

<script setup>
// --- 1. IMPORTACIONES ---
import { ref } from 'vue';
import { requestPasswordReset } from '@/services/apiInstances.js';


// --- 2. ESTADO DEL COMPONENTE ---
// Ref para el campo de email del formulario.
const email = ref('');

// Refs para gestionar los mensajes de feedback y el estado de la UI.
const message = ref('');    // Mensaje de éxito o error para el usuario.
const isError = ref(false); // Determina si el mensaje es de error (para aplicar estilos CSS).
const isLoading = ref(false); // Controla el estado de carga del botón.


// --- 3. LÓGICA PRINCIPAL ---
/**
 * @description Maneja el envío del formulario de solicitud de restablecimiento de contraseña.
 * Llama a la API y muestra un mensaje genérico al usuario por razones de seguridad.
 */
const handleRequestReset = async () => {
  // Se resetea el estado antes de cada nueva solicitud.
  message.value = '';
  isError.value = false;
  isLoading.value = true;

  // Se crea el objeto DTO (Data Transfer Object) con el email para la API.
  /** @type {import('@/api-client/index.js').ForgotPasswordDTO} */
  const forgotPasswordData = {
    email: email.value,
  };

  // Se utiliza un bloque try/catch/finally para manejar la llamada asíncrona.
  try {
    // --- Bloque de Éxito ---
    await requestPasswordReset(forgotPasswordData);
    
    // NOTA DE SEGURIDAD IMPORTANTE:
    // Se muestra siempre un mensaje de éxito genérico, incluso si el email no existe en la base de datos.
    // Esto es una práctica de seguridad crucial para prevenir la "enumeración de usuarios",
    // que es una técnica donde un atacante puede averiguar qué correos están registrados probando diferentes emails.
    message.value = 'Recibirás un correo con instrucciones para restablecer tu contraseña en breve.';
    isError.value = false;
    email.value = ''; // Se limpia el campo del formulario tras el envío.

  } catch (error) {
    // --- Bloque de Manejo de Errores ---
    // Aunque el flujo normal siempre devuelve un éxito (200 OK), este bloque
    // captura errores inesperados, como un formato de email inválido (400) o problemas de red.
    
    // Caso 1: El servidor respondió con un error (ej: 400 por email mal formado).
    if (error.response && error.response.data) {
      if (error.response.status === 400 && error.response.data.errors) {
        message.value = error.response.data.errors.join(', ');
      } else {
        message.value = error.response.data.error || 'Ocurrió un error al procesar tu solicitud.';
      }
    // Caso 2: La petición se hizo pero no hubo respuesta (error de red).
    } else if (error.request) {
      message.value = 'No se pudo conectar con el servidor. Inténtalo más tarde.';
    // Caso 3: Otro tipo de error.
    } else {
      message.value = 'Ocurrió un error inesperado.';
    }
    isError.value = true; // Marca el mensaje como un error para la UI.
    console.error("Error en la solicitud de restablecimiento:", error);

  } finally {
    // --- Bloque Final ---
    // Se ejecuta siempre, asegurando que el estado de carga se desactive
    // y el botón vuelva a estar disponible.
    isLoading.value = false;
  }
};
</script>

<style src="./RequestPasswordResetView.css" scoped></style>