<template>
  <div class="search-results-view">
    <h1>{{ pageTitle }}</h1>

    <div v-if="isLoading" class="loading-message">Cargando resultados...</div>
    <div v-else-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    <div v-else-if="!isLoading && searchResults.length === 0 && hasSearched" class="no-results-message">
      No se encontraron juegos que coincidan con los criterios.
    </div>

    <div class="results-grid" v-else-if="!isLoading && searchResults.length > 0">
      <RouterLink v-for="game in searchResults" :key="game.id"
        :to="{ name: 'game-details', params: { igdbId: game.id } }" class="game-card">
        <img :src="getCoverUrl(game.cover)" :alt="`Carátula de ${game.name || 'Juego sin nombre'}`" class="game-cover"
          @error="onImageError" />
        <div class="game-info">
          <h3 class="game-name">{{ game.name || 'Nombre no disponible' }}</h3>
          <p class="game-year" v-if="game.first_release_date">
            {{ formatReleaseYear(game.first_release_date) }}
          </p>
        </div>
      </RouterLink>
    </div>
    <div v-else class="no-results-message">
      Usa la barra de búsqueda o los filtros avanzados en el menú para encontrar juegos.
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { useRoute, RouterLink } from 'vue-router';
// Se importan las funciones específicas para buscar o filtrar juegos desde la API.
import { buscarJuegosEnIgdb, filtrarJuegosEnIgdb } from '@/services/apiInstances';
import defaultGameCover from '@/assets/img/default-game-cover.svg';

// Se inicializa 'useRoute' para poder acceder a los parámetros de la URL.
const route = useRoute();
// --- ESTADO DEL COMPONENTE ---
const searchResults = ref([]); // Almacenará los resultados de la búsqueda.
const isLoading = ref(false); // Controla el estado de carga.
const errorMessage = ref(''); // Muestra mensajes de error.
// 'hasSearched' ayuda a distinguir entre el estado inicial y una búsqueda sin resultados.
const hasSearched = ref(false);

/**
 * @description Propiedad computada que genera un título dinámico para la página
 * basándose en los parámetros de la URL.
 * @returns {string} El título a mostrar en la vista.
 */
const pageTitle = computed(() => {
  const query = route.query;
  // Si la URL tiene un parámetro 'q', es una búsqueda por texto.
  if (query.q) {
    return `Resultados para: "${query.q}"`;
  }
  // Si la URL tiene el parámetro 'filter=true', es una búsqueda avanzada.
  if (query.filter === 'true') {
    return "Resultados del Filtro Avanzado";
  }
  // Título por defecto si no hay búsqueda.
  return "Búsqueda de Juegos";
});

/**
 * @description Función central que procesa la query de la URL y decide qué
 * tipo de búsqueda realizar (texto o filtro avanzado).
 * @param {object} query - El objeto route.query con todos los parámetros.
 */
const processRouteQuery = async (query) => {
  // Si no hay parámetros en la URL, no se hace nada.
  if (Object.keys(query).length === 0) {
    searchResults.value = [];
    hasSearched.value = false;
    return;
  }

  // Se inicia el estado de carga y se limpian los resultados anteriores.
  isLoading.value = true;
  errorMessage.value = '';
  searchResults.value = [];
  hasSearched.value = true; // Se marca que se ha intentado una búsqueda.

  try {
    let response;
    // --- Lógica para decidir qué endpoint de la API llamar ---
    // Si existe el parámetro 'q', se usa la búsqueda por texto.
    if (query.q) {
      response = await buscarJuegosEnIgdb(query.q);
    // Si existe 'filter=true', se usa la búsqueda por filtros avanzados.
    } else if (query.filter === 'true') {
      response = await filtrarJuegosEnIgdb(
        query.fecha_inicio,
        query.fecha_fin,
        query.id_genero,
        query.id_tema,
        query.id_modo_juego,
        query.limite
      );
    // Si no hay parámetros reconocibles, no se hace nada.
    } else {
      isLoading.value = false;
      hasSearched.value = false;
      return;
    }

    // Se asignan los resultados al estado del componente.
    searchResults.value = response.data;
    
    // Si la búsqueda no devuelve resultados, se podría establecer un mensaje.
    // (Este mensaje se muestra en el 'v-else-if' del template).
    if (response.data.length === 0) {
      // Nota: este mensaje de error podría ser redundante si el template ya maneja el caso de array vacío.
      // errorMessage.value = "No se encontraron juegos que coincidan con los criterios."
    }

  } catch (error) {
    console.error("Error al obtener los resultados del juego:", error);
    errorMessage.value = 'No se pudieron obtener los resultados. Por favor, inténtalo de nuevo.';
  } finally {
    isLoading.value = false;
  }
};

/**
 * @description Observador (watcher) que es el principal disparador de la lógica del componente.
 * Reacciona a cualquier cambio en la query de la URL.
 */
watch(() => route.query,
  (newQuery) => {
    // Cada vez que la URL cambia (ej: /search?q=zelda), se llama a la función de procesado.
    processRouteQuery(newQuery);
  },
  {
    // 'immediate: true' asegura que el watcher se ejecute una vez al cargar el componente,
    // procesando la URL inicial con la que el usuario llega a la página.
    immediate: true,
    // 'deep: true' es importante para que el watcher detecte cambios en cualquiera
    // de las propiedades del objeto 'query', no solo si el objeto en sí es reemplazado.
    deep: true
  }
);

// --- Funciones de Utilidad (sin cambios) ---
const getCoverUrl = (cover) => {
  if (cover && typeof cover.url === 'string') {
    let imageUrl = cover.url.replace('/t_thumb/', '/t_cover_big/');
    if (imageUrl.startsWith('//')) {
      imageUrl = `https:${imageUrl}`;
    }
    return imageUrl;
  }
  return defaultGameCover;
};

const onImageError = (event) => {
  event.target.src = defaultGameCover;
};

const formatReleaseYear = (timestamp) => {
  if (!timestamp) return '';
  return new Date(Number(timestamp) * 1000).getFullYear();
};

const truncateText = (text, maxLength) => {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
};
</script>

<style src="./SearchGamesView.css" scoped></style>