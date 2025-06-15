<template>
  <div class="view-public-gamelists">
    <h1>Listas de Juegos Públicas</h1>

    <div v-if="isLoading" class="loading-message">Cargando listas públicas...</div>
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

    <div v-if="!isLoading && publicGameLists.length === 0 && !errorMessage" class="empty-message">
      Actualmente no hay listas de juegos públicas disponibles.
    </div>

    <div class="gamelists-grid" v-if="!isLoading && publicGameLists.length > 0">
      <div v-for="list in publicGameLists" :key="list.public_id" class="gamelist-card">
        <RouterLink :to="{ name: 'view-public-gamelist', params: { listPublicId: list.public_id } }" class="card-link">
          <div class="card-header">
            <h3 class="list-name">{{ list.name }}</h3>
            <span class="game-count">{{ list.game_count }} juego(s)</span>
          </div>
          <p v-if="list.description" class="list-description">{{ truncateText(list.description, 150) }}</p>
          <p v-else class="list-description-empty"><em>Sin descripción.</em></p>
          <div class="list-footer">
            <span class="owner-info">Por: {{ list.owner_username }}</span>
            <span class="last-updated" v-if="list.updated_at">Última act.: {{ formatReadableDate(list.updated_at) }}</span>
          </div>
        </RouterLink>
      </div>
    </div>
  </div>
</template>
<script setup>
// --- 1. IMPORTACIONES ---
import { ref, onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import { fetchAllPublicGameLists } from '@/services/apiInstances';


// --- 2. ESTADO DEL COMPONENTE ---
const publicGameLists = ref([]); // Almacenará el array de listas de juegos públicas.
const isLoading = ref(true);     // Controla el mensaje de carga principal.
const errorMessage = ref('');    // Almacena y muestra mensajes de error de la API.


// --- 3. CICLO DE VIDA ---
/**
 * @description onMounted se ejecuta cuando el componente está listo en el DOM.
 * Es el lugar ideal para realizar la carga inicial de datos.
 */
onMounted(() => {
  loadPublicGameLists();
});


// --- 4. MÉTODOS DE DATOS ---
/**
 * @description Carga todas las listas de juegos públicas desde la API.
 * Gestiona los estados de carga y error para la vista.
 */
const loadPublicGameLists = async () => {
  isLoading.value = true;
  errorMessage.value = '';
  try {
    const response = await fetchAllPublicGameLists();
    publicGameLists.value = response.data;
  } catch (error) {
    console.error("Error cargando listas públicas de juegos:", error);
    // Se intenta dar un mensaje de error más específico si la API lo proporciona.
    if (error.response) {
      errorMessage.value = `Error ${error.response.status}: ${error.response.data.message || error.response.data.error || 'No se pudieron cargar las listas.'}`;
    } else {
      errorMessage.value = 'Error de red al cargar las listas.';
    }
  } finally {
    // Este bloque se ejecuta siempre, asegurando que el estado de carga se desactive.
    isLoading.value = false;
  }
};


// --- 5. FUNCIONES DE UTILIDAD (HELPERS) ---
/**
 * @description Formatea una fecha en formato ISO a un string legible (ej: 'jun 15, 2025').
 * @param {string} isoDateString - La fecha en formato ISO.
 * @returns {string} - La fecha formateada o el string original en caso de error.
 */
const formatReadableDate = (isoDateString) => {
  if (!isoDateString) return '';
  try {
    return new Date(isoDateString).toLocaleDateString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  } catch (e) {
    return isoDateString;
  }
};

/**
 * @description Trunca un texto si excede una longitud máxima, añadiendo puntos suspensivos.
 * @param {string} text - El texto a truncar.
 * @param {number} maxLength - La longitud máxima permitida.
 * @returns {string} - El texto truncado o el original si es más corto.
 */
const truncateText = (text, maxLength) => {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
};
</script>
<style src="./ViewPublicGameListsView.css" scoped>

</style>