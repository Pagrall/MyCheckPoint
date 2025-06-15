import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia' 
import App from './App.vue'
import router from './router'

import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'

import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const app = createApp(App)

const vuetify = createVuetify({
    components,  // Registra todos los componentes importados
    directives,  // Registra todas las directivas importadas
    // Aquí podrías añadir configuraciones adicionales de Vuetify, como temas:
     /*theme: {
       defaultTheme: 'light', // o 'dark'
       themes: {
         light: {
           dark: false,
           colors: {
             primary: '#1976D2', // Azul
             secondary: '#424242', // Gris oscuro
             // ... otros colores
           }
         },
         dark: {
           dark: true,
           colors: {
             primary: '#2196F3',
             secondary: '#616161',
             // ... otros colores
           }
         }
       }
     }*/
})

app.use(createPinia()) 
app.use(router)
app.use(vuetify)

app.mount('#app')
