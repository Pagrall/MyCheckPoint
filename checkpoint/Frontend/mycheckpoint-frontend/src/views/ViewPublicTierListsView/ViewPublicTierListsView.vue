<template>
  <div class="view-public-tierlists">
    <h1>Tier Lists Públicas</h1>

    <div v-if="isLoading" class="loading-message">Cargando tier lists públicas...</div>
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

    <div v-if="!isLoading && publicTierLists.length === 0 && !errorMessage" class="empty-message">
      Actualmente no hay tier lists públicas disponibles.
    </div>

    <div class="tierlists-grid" v-if="!isLoading && publicTierLists.length > 0">
      <div v-for="tierList in publicTierLists" :key="tierList.public_id" class="tierlist-card">
        <RouterLink :to="{ name: 'view-public-tierlist', params: { tierListPublicId: tierList.public_id } }" class="card-link">
          <div class="card-header">
            <h3 class="list-name">{{ tierList.name }}</h3>
            
          </div>
          <p v-if="tierList.description" class="list-description">{{ truncateText(tierList.description, 150) }}</p>
          <p v-else class="list-description-empty"><em>Sin descripción.</em></p>
          <div class="list-footer">
            <span class="owner-info">Por: {{ tierList.owner_username }}</span>
            <span class="last-updated" v-if="tierList.updated_at">Última act.: {{ formatReadableDate(tierList.updated_at) }}</span>
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
import { fetchAllPublicTierLists } from '@/services/apiInstances';


// --- 2. ESTADO DEL COMPONENTE ---
const publicTierLists = ref([]); // Almacenará el array de Tier Lists públicas.
const isLoading = ref(true);     // Controla el mensaje de carga principal.
const errorMessage = ref('');    // Almacena y muestra mensajes de error de la API.


// --- 3. CICLO DE VIDA ---
/**
 * @description onMounted se ejecuta cuando el componente está listo en el DOM.
 * Es el lugar ideal para realizar la carga inicial de datos.
 */
onMounted(() => {
  loadPublicTierLists();
});


// --- 4. MÉTODOS DE DATOS ---
/**
 * @description Carga todas las Tier Lists públicas desde la API.
 * Gestiona los estados de carga y error para la vista.
 */
const loadPublicTierLists = async () => {
  isLoading.value = true;
  errorMessage.value = '';
  try {
    const response = await fetchAllPublicTierLists();
    publicTierLists.value = response.data;
  } catch (error) {
    console.error("Error cargando tier lists públicas:", error);
    // Se intenta dar un mensaje de error más específico si la API lo proporciona.
    if (error.response) {
      errorMessage.value = `Error ${error.response.status}: ${error.response.data.message || error.response.data.error || 'No se pudieron cargar las tier lists.'}`;
    } else {
      errorMessage.value = 'Error de red al cargar las tier lists.';
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

/**
 * @description Convierte el tipo de Tier List (devuelto por la API) en un texto legible.
 * Nota: Esta función está definida pero no se utiliza en el template actual.
 * @param {string} type - El tipo de Tier List (ej. 'PROFILE_GLOBAL').
 * @returns {string} - El texto formateado para mostrar en la UI.
 */
const formatTierListType = (type) => { 
  if (!type) return 'Tipo Desconocido';
  const typeMap = {
    'PROFILE_GLOBAL': 'De Perfil',
    'FROM_GAMELIST': 'Basada en Lista de Juegos'
  };
  return typeMap[type] || type;
};
</script>
<style src="./ViewPublicTierListsView.css" scoped>

</style>