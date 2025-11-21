<script setup lang="ts">
import Navbar from '../components/Navbar.vue'
import HeroCarousel from '../components/HeroCarousel.vue'
import AboutSection from '../components/AboutSection.vue'
import ContactSection from '../components/ContactSection.vue'
import Footer from '../components/Footer.vue'
</script>

<template>
  <div class="smooth-scroll">
    <Navbar />
    <main>
      <HeroCarousel />
      <AboutSection />
      <ContactSection />
    </main>
    <Footer />
  </div>
</template>

<style scoped>
html {
  scroll-behavior: smooth;
}
</style>

