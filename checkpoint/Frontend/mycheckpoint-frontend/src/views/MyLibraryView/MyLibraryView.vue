<template>
  <div class="my-library-view">
    <div class="filters-panel section-block">
      <div class="filter-controls">
        <h3 class="filters-title">Filtrar Biblioteca</h3>
        <div class="filter-group">
          <label for="filter-status">Estado</label>
          <select id="filter-status" v-model="selectedStatus">
            <option v-for="opt in gameStatusOptions" :key="opt.value" :value="opt.value">
              {{ opt.text }}
            </option>
          </select>
        </div>
        <div class="filter-group">
          <label for="filter-platform">Plataforma</label>
          <select id="filter-platform" v-model="selectedPlatform">
            <option v-for="opt in personalPlatformOptions" :key="opt.value" :value="opt.value">
              {{ opt.text }}
            </option>
          </select>
        </div>
        <div class="filter-group">
          <label for="filter-score">Puntuación Mínima</label>
          <select id="filter-score" v-model.number="selectedMinScore">
             <option v-for="opt in scoreOptions" :key="opt.value" :value="opt.value">
              {{ opt.text }}
            </option>
          </select>
        </div>
        <div class="filter-actions">
          <button @click="resetFilters" class="button-reset">
            <span class="icon-reset">&times;</span> Limpiar
          </button>
        </div>
      </div>
    </div>
    <div v-if="isLoading" class="loading-message">Cargando mi biblioteca...</div>
    <div v-else-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    <div v-else-if="filteredLibraryGames.length === 0" class="empty-library-message">
      <span v-if="myLibraryGames.length === 0 && !selectedStatus && !selectedPlatform && selectedMinScore === null">
        Aún no tienes juegos en tu biblioteca. ¡Empieza a añadir algunos! 
      </span>
      <span v-else>
        No hay juegos que coincidan con tus filtros actuales.
      </span>
    </div>

    <div class="library-grid" v-if="!isLoading && filteredLibraryGames.length > 0">
      <div v-for="gameEntry in filteredLibraryGames" :key="gameEntry.game_igdb_id" class="library-game-card">
        <RouterLink :to="{ name: 'game-details', params: { igdbId: gameEntry.game_igdb_id } }" class="card-full-link">
          <div class="card-cover-container">
            <img 
              :src="getCoverUrl(gameEntry.game_cover, 't_cover_big')" 
              :alt="`Carátula de ${gameEntry.game_name || 'Juego'}`" 
              class="library-game-cover" 
              @error="onLibraryCoverError" 
            />
            <div v-if="gameEntry.score !== null && gameEntry.score !== undefined"
                 class="score-badge-on-cover" 
                 :class="getScoreColorClass(gameEntry.score)">
              {{ gameEntry.score }}
            </div>
            <div v-if="gameEntry.personal_platform" class="platform-logo-on-cover">
              <img :src="getPlatformLogoUrl(gameEntry.personal_platform)" :alt="gameEntry.personal_platform" />
            </div>
          </div>
          <div class="card-content">
            <h3 class="game-title">{{ gameEntry.game_name || `Juego ID: ${gameEntry.game_igdb_id}` }}</h3> 
          </div>
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
// --- 1. IMPORTACIONES Y CONFIGURACIÓN INICIAL ---
import { ref, onMounted, computed } from 'vue';
import { RouterLink } from 'vue-router';
import { getMyUserGameLibrary } from '@/services/apiInstances';
import { useAuthStore } from '@/stores/authStore';
import defaultLibraryCover from '@/assets/img/default-game-cover.svg';

// Se inicializa el store de autenticación para comprobar si el usuario ha iniciado sesión.
const authStore = useAuthStore();


// --- 2. ESTADO DEL COMPONENTE ---
// -- Estado principal de la vista --
const myLibraryGames = ref([]); // Almacenará la biblioteca completa y sin filtrar del usuario.
const isLoading = ref(true);    // Controla el mensaje de carga principal.
const errorMessage = ref('');   // Almacena y muestra errores de carga.

// -- Estado para los filtros --
// Cada ref está vinculado a un <select> en el template para la reactividad.
const selectedStatus = ref(null);
const selectedPlatform = ref(null);
const selectedMinScore = ref(null);


// --- 3. DATOS ESTÁTICOS (Opciones para Filtros) ---
// Arrays de objetos que definen las opciones disponibles en los menús desplegables de los filtros.
const gameStatusOptions = [
  { value: null, text: 'Todos los Estados' },
  { value: 'PLAYING', text: 'Jugando' },
  { value: 'PLAYING_PAUSED', text: 'Jugando (En Pausa)' },
  { value: 'PLAYING_ENDLESS', text: 'Jugando (Sin Fin)' },
  { value: 'COMPLETED_MAIN_STORY', text: 'Historia Principal Completada' },
  { value: 'COMPLETED_MAIN_AND_SIDES', text: 'Principal + Secundarias Completado' },
  { value: 'COMPLETED_100_PERCENT', text: 'Completado al 100%' },
  { value: 'WISHLIST', text: 'En Lista de Deseos' },
  { value: 'ARCHIVED_ABANDONED', text: 'Archivado (Abandonado)' },
  { value: 'ARCHIVED_NOT_PLAYING', text: 'Archivado (Sin Jugar)' },
  { value: 'ARCHIVED', text: 'Archivado (Otro)' },
];
const personalPlatformOptions = [
  { value: null, text: 'Todas las Plataformas' },
  { value: 'STEAM', text: 'Steam' },
  { value: 'EPIC_GAMES', text: 'Epic Games Store' },
  { value: 'GOG_GALAXY', text: 'GOG Galaxy' },
  { value: 'XBOX', text: 'Xbox' },
  { value: 'PLAYSTATION', text: 'PlayStation' },
  { value: 'NINTENDO', text: 'Nintendo' },
  { value: 'BATTLE_NET', text: 'Battle.net' },
  { value: 'EA_APP', text: 'EA App' },
  { value: 'UBISOFT_CONNECT', text: 'Ubisoft Connect' },
  { value: 'OTHER', text: 'Otra' },
];
const scoreOptions = [
    { value: null, text: 'Cualquier Puntuación' },
    { value: 0, text: 'Sin Puntuar' },
    { value: 1, text: '1+' },
    { value: 2, text: '2+' },
    { value: 3, text: '3+' },
    { value: 4, text: '4+' },
    { value: 5, text: '5+' },
    { value: 6, text: '6+' },
    { value: 7, text: '7+' },
    { value: 8, text: '8+' },
    { value: 9, text: '9+' },
    { value: 10, text: '10 (Perfecta)' }
];


// --- 4. PROPIEDAD COMPUTADA PARA FILTRAR ---
/**
 * @description Propiedad computada que devuelve la lista de juegos filtrada.
 * Se recalcula automáticamente cada vez que cambia uno de los filtros seleccionados
 * o la biblioteca principal de juegos.
 * @returns {Array} - Un array con los juegos que cumplen los criterios de filtrado.
 */
const filteredLibraryGames = computed(() => {
  // Si la biblioteca no se ha cargado, devuelve un array vacío.
  if (!myLibraryGames.value) return [];
  
  // El método filter itera sobre cada juego y devuelve solo aquellos que cumplen todas las condiciones.
  return myLibraryGames.value.filter(gameEntry => {
    // Comprueba si el juego coincide con el estado seleccionado (o si no hay filtro de estado).
    const statusMatch = !selectedStatus.value || gameEntry.status === selectedStatus.value;
    
    // Comprueba si el juego coincide con la plataforma seleccionada (o si no hay filtro de plataforma).
    const platformMatch = !selectedPlatform.value || gameEntry.personal_platform === selectedPlatform.value;
    
    // Lógica para la puntuación.
    let scoreMatch = true;
    if (selectedMinScore.value !== null && selectedMinScore.value !== undefined) {
      // Caso especial: si se selecciona "Sin Puntuar", se buscan juegos con puntuación nula, indefinida o 0.
      if (selectedMinScore.value === 0) {
        scoreMatch = gameEntry.score === null || gameEntry.score === undefined || gameEntry.score === 0;
      } else {
        // Para el resto de puntuaciones, se buscan juegos con puntuación mayor o igual a la seleccionada.
        scoreMatch = gameEntry.score !== null && gameEntry.score !== undefined && gameEntry.score >= selectedMinScore.value;
      }
    }

    // Un juego solo se incluye en el resultado si todas las condiciones son verdaderas.
    return statusMatch && platformMatch && scoreMatch;
  });
});


// --- 5. CICLO DE VIDA ---
/**
 * @description Se ejecuta cuando el componente se monta. Comprueba si el usuario está
 * autenticado antes de intentar cargar la biblioteca.
 */
onMounted(() => {
  if (authStore.isAuthenticated) {
    fetchLibrary();
  } else {
    errorMessage.value = "Debes iniciar sesión para ver tu biblioteca.";
    isLoading.value = false;
  }
});


// --- 6. MÉTODOS DE DATOS Y ACCIONES ---
/**
 * @description Carga la biblioteca de juegos del usuario desde la API.
 */
const fetchLibrary = async () => {
  isLoading.value = true;
  errorMessage.value = '';
  try {
    const response = await getMyUserGameLibrary();
    myLibraryGames.value = response.data;
  } catch (error) {
    console.error("Error cargando la biblioteca:", error);
    errorMessage.value = 'Error de red al cargar tu biblioteca.';
  } finally {
    isLoading.value = false;
  }
};

/**
 * @description Resetea todos los filtros a su estado inicial.
 */
const resetFilters = () => {
  selectedStatus.value = null;
  selectedPlatform.value = null;
  selectedMinScore.value = null;
};


// --- 7. FUNCIONES DE UTILIDAD (HELPERS) ---
/**
 * @description Devuelve una clase CSS para colorear la puntuación según su valor.
 * @param {number} score - La puntuación del juego.
 * @returns {string} - El nombre de la clase CSS.
 */
const getScoreColorClass = (score) => {
  if (score >= 8) return 'score-high';
  if (score >= 5) return 'score-medium';
  if (score > 0) return 'score-low';
  return 'score-none'; // Para puntuaciones de 0 o nulas.
};

/**
 * @description Construye la URL completa y correcta para el logo de una plataforma.
 * @param {string} platform - El identificador de la plataforma (ej: 'STEAM').
 * @returns {string} - La URL del archivo SVG del logo.
 */
const getPlatformLogoUrl = (platform) => {
  if (!platform) return '';
  const platformName = platform.toLowerCase();
  try {
    // Esta sintaxis con `new URL` es necesaria para que empaquetadores como Vite
    // puedan resolver correctamente las rutas de assets dinámicos.
    return new URL(`/src/assets/logo-personal-platform/${platformName}.svg`, import.meta.url).href;
  } catch (error) {
    console.warn(`No se encontró el logo para la plataforma: ${platformName}.svg`);
    // Devuelve un logo genérico si el específico no se encuentra.
    return new URL(`/src/assets/logo-personal-platform/other.svg`, import.meta.url).href;
  }
};

/**
 * @description Construye la URL de la carátula de un juego con el tamaño deseado.
 * @param {object} coverData - El objeto 'cover' de la API.
 * @param {string} targetSizeFallback - El tamaño de imagen de IGDB deseado.
 * @returns {string} - La URL completa de la imagen o un placeholder.
 */
const getCoverUrl = (coverData, targetSizeFallback = 't_cover_big') => {
  if (coverData && typeof coverData.url === 'string' && coverData.url.trim() !== '') {
    let imageUrl = coverData.url;
    if (imageUrl.startsWith('//')) { imageUrl = `https://` + imageUrl; }
    // Intenta reemplazar el tamaño de la imagen en la URL.
    if (imageUrl.includes('/t_thumb/')) {
      imageUrl = imageUrl.replace('/t_thumb/', `/${targetSizeFallback}/`);
    } else if (imageUrl.includes('/t_cover_small/')) {
      imageUrl = imageUrl.replace('/t_cover_small/', `/${targetSizeFallback}/`);
    }
    return imageUrl;
  }
  return defaultLibraryCover;
};

/**
 * @description Fallback para cuando una carátula de juego no se puede cargar.
 * @param {Event} event - El evento de error de la imagen.
 */
const onLibraryCoverError = (event) => {
  event.target.src = defaultLibraryCover;
};
</script>

<style src="./MyLibraryView.css" scoped></style>