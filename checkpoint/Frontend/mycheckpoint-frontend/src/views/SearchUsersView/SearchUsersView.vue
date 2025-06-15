<template>
  <div class="search-users-view">
    <h1>Buscar Usuarios</h1>
    
    <form @submit.prevent="executeSearch" class="search-form">
      <input 
        type="text" 
        v-model="searchQuery" 
        placeholder="Escribe un nombre de usuario..."
        class="search-input"
        :disabled="isLoading"
      />
      <button type="submit" class="search-button" :disabled="isLoading || searchQuery.length < 2">
        {{ isLoading ? 'Buscando...' : 'Buscar' }}
      </button>
    </form>
    <small v-if="searchQuery.length > 0 && searchQuery.length < 2" class="input-hint">
      Necesitas al menos 2 caracteres para buscar.
    </small>

    <div v-if="isLoading" class="loading-message">Buscando usuarios...</div>
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    <div v-if="searched && !isLoading && searchResults.length === 0 && !errorMessage" class="no-results-message">
      No se encontraron usuarios que coincidan con tu búsqueda.
    </div>

    <div class="results-grid" v-if="searchResults.length > 0">
      <div v-for="user in searchResults" :key="user.public_id" class="user-card">
        <RouterLink :to="{ name: 'profile', params: { publicId: user.public_id } }">
          <img 
            :src="getProfilePictureUrl(user.foto_perfil)" 
            alt="Avatar del usuario" 
            class="user-avatar"
            @error="onAvatarError"
          />
          <div class="user-info">
            <h3 class="user-name">{{ user.nombre_usuario }}</h3>
          </div>
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { RouterLink } from 'vue-router';
import { searchUsers } from '@/services/apiInstances'; // Importa la función para buscar usuarios.
import { BASE_PATH } from '@/api-client/base'; // Importa la URL base de la API.
import defaultAvatar from '@/assets/img/default-avatar.svg'; // Imagen placeholder para avatares.

// --- ESTADO DEL COMPONENTE ---
const searchQuery = ref(''); // Almacena el texto introducido por el usuario en el campo de búsqueda.
const searchResults = ref([]); // Almacenará los usuarios encontrados por la API.
const isLoading = ref(false); // Controla el estado de carga para mostrar mensajes y deshabilitar controles.
const errorMessage = ref(''); // Almacena mensajes de error para mostrarlos en la UI.
const searched = ref(false); // Un flag para saber si ya se ha ejecutado una búsqueda. Ayuda a distinguir el estado inicial de "aún no has buscado" del estado "has buscado y no hay resultados".

/**
 * @description Función principal que se ejecuta al enviar el formulario de búsqueda.
 */
const executeSearch = async () => {
  // Validación del lado del cliente para evitar llamadas a la API con texto muy corto.
  if (searchQuery.value.length < 2) {
    errorMessage.value = "Por favor, introduce al menos 2 caracteres para buscar.";
    return;
  }
  
  // Reseteo de estados al iniciar una nueva búsqueda.
  isLoading.value = true;
  errorMessage.value = '';
  searchResults.value = [];
  searched.value = true; // Se marca que se ha iniciado el proceso de búsqueda.

  try {
    // Se llama a la función del servicio de API pasándole el texto de búsqueda.
    const response = await searchUsers(searchQuery.value);
    // Si la llamada es exitosa, se guardan los resultados en el estado.
    searchResults.value = response.data;
  } catch (error) {
    // --- Bloque de Manejo de Errores ---
    console.error("Error buscando usuarios:", error);
    // Caso 1: El servidor respondió, pero con un código de error.
    if (error.response && error.response.status === 404) {
      // Un error 404 aquí significa que la búsqueda fue válida pero no encontró coincidencias.
      errorMessage.value = `No se encontraron usuarios para "${searchQuery.value}".`;
    } else if (error.response) {
      // Cualquier otro error del servidor (ej. 500, 401, etc.).
      errorMessage.value = `Error ${error.response.status}: ${error.response.data.message || 'No se pudo realizar la búsqueda.'}`;
    } else {
      // Caso 2: Error de red, la petición no llegó al servidor.
      errorMessage.value = "Error de red al realizar la búsqueda.";
    }
    // Se asegura de que los resultados estén vacíos si hubo un error.
    searchResults.value = [];
  } finally {
    // --- Bloque Final ---
    // Se ejecuta siempre, tanto en caso de éxito como de error, para desactivar el estado de carga.
    isLoading.value = false;
  }
};

/**
 * @description Construye la URL absoluta para la imagen de perfil de un usuario.
 * @param {string | null} fotoPerfil - La ruta de la imagen de perfil que viene de la API.
 * @returns {string} La URL completa y utilizable para el `src` de una imagen.
 */
const getProfilePictureUrl = (fotoPerfil) => {
  // Si el campo 'fotoPerfil' existe y no está vacío...
  if (fotoPerfil) {
    // Si ya es una URL completa, la devuelve directamente.
    if (fotoPerfil.startsWith('http')) {
      return fotoPerfil;
    }
    // Si es una ruta relativa, construye la URL completa usando la base de la API.
    const baseApiUrl = BASE_PATH.endsWith('/') ? BASE_PATH.slice(0, -1) : BASE_PATH;
    const relativeImagePath = fotoPerfil.startsWith('/') ? fotoPerfil.substring(1) : fotoPerfil;
    return `${baseApiUrl}/profile-pictures/${relativeImagePath}`;
  }
  // Si no hay 'fotoPerfil', devuelve el avatar por defecto.
  return defaultAvatar;
};

/**
 * @description Función de fallback que se ejecuta si una imagen de avatar no se puede cargar.
 * Asigna una imagen por defecto al elemento 'img' que ha fallado.
 * @param {Event} event - El evento de error de la imagen.
 */
const onAvatarError = (event) => {
  event.target.src = defaultAvatar;
};
</script>

<style src="./SearchUsersView.css" scoped> </style>