<template>
  <footer class="main-footer">
    <div class="footer-content">

      <div class="footer-section about-section">
        <h4 class="footer-title">Fuente de Datos</h4>
        <a href="https://www.igdb.com/" target="_blank" rel="noopener noreferrer" class="igdb-logo-link">
          <img src="../../assets/img/igdb.svg" alt="IGDB Logo" class="igdb-logo">
        </a>
        <p class="data-notice">
          Los datos y las imágenes de los videojuegos han sido extraídos gratuitamente a través de la API de IGDB.
        </p>
      </div>

      <div class="footer-section tech-section">
        <h4 class="footer-title">Tecnologías</h4>
        <ul class="tech-list">
          <li>Spring Boot</li>
          <li>PostgreSQL</li>
          <li>Vue.js</li>
        </ul>
      </div>

      <div class="footer-section team-section">
        <h4 class="footer-title">Equipo</h4>
        <ul class="team-list">
          <li><a href="mailto:pgraalc112@g.educaand.es">Pablo Granero Alcalá</a></li>
          <li><a href="mailto:mfercob430@g.educaand.es">Mario Ferro Cobo</a></li>
        </ul>
        
      </div>

      <div class="footer-section team-section">
        <h4 class="footer-title">Soporte Técnico</h4>
        <p><a href="mailto:mycheckpoint.supp@gmail.com">mycheckpoint.supp@gmail.com</a></p>
      </div>

    </div>
    <div class="footer-bottom">
      <p>© {{ new Date().getFullYear() }} MyCheckPoint. Todos los derechos reservados.</p>
    </div>
  </footer>
</template>

<style src="./Footer.css" scoped></style>