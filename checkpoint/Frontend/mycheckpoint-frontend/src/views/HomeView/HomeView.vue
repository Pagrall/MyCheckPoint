<template>
  <div class="home-view">
    <h1>Explorar Juegos</h1>

    <section class="game-row">
      <h2>Más Populares</h2>
      <div v-if="isLoading.hyped" class="loading-message">Cargando...</div>
      <div v-if="errors.hyped" class="error-message">{{ errors.hyped }}</div>
      
      <div v-else class="carousel-wrapper">
        <button @click="scrollCarousel(mostHypedCarouselRef, 'prev')" class="carousel-arrow prev" aria-label="Anterior">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5 8.25 12l7.5-7.5" /></svg>
        </button>
        
        <div class="games-carousel" ref="mostHypedCarouselRef">
          <RouterLink v-for="game in mostHyped" :key="game.id" :to="{ name: 'game-details', params: { igdbId: game.id } }" class="game-card">
            <img :src="getCoverUrl(game.cover)" :alt="game.name" class="game-cover" @error.self="onImageError">
            <span class="game-name">{{ game.name }}</span>
          </RouterLink>
        </div>
        
        <button @click="scrollCarousel(mostHypedCarouselRef, 'next')" class="carousel-arrow next" aria-label="Siguiente">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="m8.25 4.5 7.5 7.5-7.5 7.5" /></svg>
        </button>
      </div>
    </section>


    <section class="game-row">
      <h2>Próximos Lanzamientos</h2>
      <div v-if="isLoading.upcoming" class="loading-message">Cargando...</div>
      <div v-if="errors.upcoming" class="error-message">{{ errors.upcoming }}</div>

      <div v-else class="carousel-wrapper">
        <button @click="scrollCarousel(upcomingReleasesCarouselRef, 'prev')" class="carousel-arrow prev" aria-label="Anterior">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5 8.25 12l7.5-7.5" /></svg>
        </button>
        
        <div class="games-carousel" ref="upcomingReleasesCarouselRef">
          <RouterLink v-for="game in upcomingReleases" :key="game.id" :to="{ name: 'game-details', params: { igdbId: game.id } }" class="game-card">
            <img :src="getCoverUrl(game.cover)" :alt="game.name" class="game-cover" @error.self="onImageError">
            <span class="game-name">{{ game.name }}</span>
          </RouterLink>
        </div>
        
        <button @click="scrollCarousel(upcomingReleasesCarouselRef, 'next')" class="carousel-arrow next" aria-label="Siguiente">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="m8.25 4.5 7.5 7.5-7.5 7.5" /></svg>
        </button>
      </div>
    </section>

    <section class="game-row">
      <h2>Más Esperado</h2>
      <div v-if="isLoading.anticipated" class="loading-message">Cargando...</div>
      <div v-if="errors.anticipated" class="error-message">{{ errors.anticipated }}</div>
      
      <div v-else class="carousel-wrapper">
        <button @click="scrollCarousel(highlyAnticipatedCarouselRef, 'prev')" class="carousel-arrow prev" aria-label="Anterior">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5 8.25 12l7.5-7.5" /></svg>
        </button>
        
        <div class="games-carousel" ref="highlyAnticipatedCarouselRef">
          <RouterLink v-for="game in highlyAnticipated" :key="game.id" :to="{ name: 'game-details', params: { igdbId: game.id } }" class="game-card">
            <img :src="getCoverUrl(game.cover)" :alt="game.name" class="game-cover" @error.self="onImageError">
            <span class="game-name">{{ game.name }}</span>
          </RouterLink>
        </div>
        
        <button @click="scrollCarousel(highlyAnticipatedCarouselRef, 'next')" class="carousel-arrow next" aria-label="Siguiente">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="m8.25 4.5 7.5 7.5-7.5 7.5" /></svg>
        </button>
      </div>
    </section>

    <section class="game-row">
      <h2>Lanzados Recientemente</h2>
      <div v-if="isLoading.recently" class="loading-message">Cargando...</div>
      <div v-if="errors.recently" class="error-message">{{ errors.recently }}</div>
      
      <div v-else class="carousel-wrapper">
        <button @click="scrollCarousel(recentlyReleasedCarouselRef, 'prev')" class="carousel-arrow prev" aria-label="Anterior">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5 8.25 12l7.5-7.5" /></svg>
        </button>
        
        <div class="games-carousel" ref="recentlyReleasedCarouselRef">
          <RouterLink v-for="game in recentlyReleased" :key="game.id" :to="{ name: 'game-details', params: { igdbId: game.id } }" class="game-card">
            <img :src="getCoverUrl(game.cover)" :alt="game.name" class="game-cover" @error.self="onImageError">
            <span class="game-name">{{ game.name }}</span>
          </RouterLink>
        </div>
        
        <button @click="scrollCarousel(recentlyReleasedCarouselRef, 'next')" class="carousel-arrow next" aria-label="Siguiente">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="m8.25 4.5 7.5 7.5-7.5 7.5" /></svg>
        </button>
      </div>
    </section>

  </div>
</template>

<script setup>
// --- 1. IMPORTACIONES ---
import { ref, reactive, onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import {
  findRecentlyReleasedGames,
  findMostHypedGames,
  findHighlyAnticipatedGames,
  findUpcomingReleases
} from '@/services/apiInstances';
import defaultGameCover from '@/assets/img/default-game-cover.svg';


// --- 2. REFERENCIAS A ELEMENTOS DEL DOM ---
// Estas refs se conectan con los elementos 'ref' en el <template> para poder manipularlos,
// en este caso, para controlar el desplazamiento de cada carrusel de juegos.
const mostHypedCarouselRef = ref(null);
const upcomingReleasesCarouselRef = ref(null);
const highlyAnticipatedCarouselRef = ref(null);
const recentlyReleasedCarouselRef = ref(null);


// --- 3. ESTADO DEL COMPONENTE ---
// -- Listas de Juegos --
// Cada ref almacenará un array de juegos devuelto por la API.
const recentlyReleased = ref([]);
const mostHyped = ref([]);
const highlyAnticipated = ref([]);
const upcomingReleases = ref([]);

// -- Estados de Carga --
// Un objeto reactivo para gestionar el estado de carga de cada sección de forma independiente.
// Esto permite que unas secciones se muestren mientras otras todavía están cargando.
const isLoading = reactive({
  recently: true,
  hyped: true,
  anticipated: true,
  upcoming: true
});

// -- Estados de Error --
// Un objeto reactivo para almacenar los mensajes de error de cada sección por separado.
const errors = reactive({
  recently: '',
  hyped: '',
  anticipated: '',
  upcoming: ''
});


// --- 4. CICLO DE VIDA (onMounted) ---
/**
 * @description Se ejecuta una vez que el componente se ha montado en el DOM.
 * Realiza todas las llamadas a la API para obtener los datos de los juegos.
 */
onMounted(async () => {
  // Se utiliza Promise.allSettled para ejecutar todas las peticiones de forma concurrente.
  // A diferencia de Promise.all, allSettled espera a que TODAS las promesas terminen,
  // ya sea con éxito o con error. Esto es ideal aquí para que si una sección falla,
  // las demás puedan seguir cargando y mostrándose correctamente.
  const [recentlyRes, hypedRes, anticipatedRes, upcomingRes] = await Promise.allSettled([
    findRecentlyReleasedGames(),
    findMostHypedGames(),
    findHighlyAnticipatedGames(),
    findUpcomingReleases()
  ]);

  // Procesar resultados para "Lanzamientos Recientes"
  // Se comprueba el estado de la promesa. Si es 'fulfilled', la petición tuvo éxito.
  if (recentlyRes.status === 'fulfilled') {
    recentlyReleased.value = recentlyRes.value.data;
  } else {
    // Si la promesa fue 'rejected', se registra el error y se asigna un mensaje para la UI.
    errors.recently = 'No se pudieron cargar los lanzamientos recientes.';
    console.error('Error en findRecentlyReleasedGames:', recentlyRes.reason);
  }
  // En cualquier caso (éxito o error), se desactiva el estado de carga para esta sección.
  isLoading.recently = false;

  // Procesar resultados para "Más Populares"
  if (hypedRes.status === 'fulfilled') {
    mostHyped.value = hypedRes.value.data;
  } else {
    errors.hyped = 'No se pudieron cargar los juegos más populares.';
    console.error('Error en findMostHypedGames:', hypedRes.reason);
  }
  isLoading.hyped = false;

  // Procesar resultados para "Más Esperados"
  if (anticipatedRes.status === 'fulfilled') {
    highlyAnticipated.value = anticipatedRes.value.data;
  } else {
    errors.anticipated = 'No se pudieron cargar los juegos más anticipados.';
    console.error('Error en findHighlyAnticipatedGames:', anticipatedRes.reason);
  }
  isLoading.anticipated = false;

  // Procesar resultados para "Próximos Lanzamientos"
  if (upcomingRes.status === 'fulfilled') {
    upcomingReleases.value = upcomingRes.value.data;
  } else {
    errors.upcoming = 'No se pudieron cargar los próximos lanzamientos.';
    console.error('Error en findUpcomingReleases:', upcomingRes.reason);
  }
  isLoading.upcoming = false;
});


// --- 5. MÉTODOS DE UI ---
/**
 * @description Controla el desplazamiento horizontal de un carrusel.
 * @param {object} carouselRef - La referencia al elemento del DOM del carrusel.
 * @param {string} direction - La dirección del desplazamiento ('prev' o 'next').
 */
const scrollCarousel = (carouselRef, direction) => {
  // Se asegura de que la referencia al carrusel existe.
  if (carouselRef) {
    // Calcula la distancia de desplazamiento como un porcentaje del ancho visible del carrusel.
    // Esto hace que el desplazamiento sea responsivo al tamaño de la ventana.
    const scrollAmount = carouselRef.clientWidth * 0.5;
    
    // Utiliza el método 'scrollBy' con un comportamiento 'smooth' para una animación suave.
    carouselRef.scrollBy({
      left: direction === 'next' ? scrollAmount : -scrollAmount,
      behavior: 'smooth'
    });
  }
};


// --- 6. FUNCIONES DE UTILIDAD ---
/**
 * @description Construye la URL correcta para la carátula de un juego a partir de los datos de la API de IGDB.
 * @param {object} cover - El objeto 'cover' que viene en los datos del juego.
 * @returns {string} - La URL completa y formateada de la imagen o un placeholder por defecto.
 */
const getCoverUrl = (cover) => {
  // Comprueba que el objeto 'cover' y su propiedad 'url' existan.
  if (cover && typeof cover.url === 'string') {
    // Reemplaza el tamaño de la imagen en la URL para obtener una de mayor calidad.
    let imageUrl = cover.url.replace('/t_thumb/', '/t_cover_big/');
    
    // Corrige las URLs que empiezan por '//' añadiendo el protocolo 'https'.
    if (imageUrl.startsWith('//')) {
      return `https:${imageUrl}`;
    }
    return imageUrl;
  }
  // Si no hay URL, devuelve una imagen por defecto.
  return defaultGameCover;
};

/**
 * @description Función de fallback que se ejecuta si una imagen no se puede cargar.
 * Asigna una imagen por defecto al elemento 'img' que ha fallado.
 * @param {Event} event - El evento de error de la imagen.
 */
const onImageError = (event) => {
  event.target.src = defaultGameCover;
};
</script>

<style src="./HomeView.css" scoped>

</style>