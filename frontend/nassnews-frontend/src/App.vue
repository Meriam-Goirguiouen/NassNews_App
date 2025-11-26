<script setup lang="ts">
import { onMounted, ref } from 'vue'
import Navbar from './components/Navbar.vue'
import HeroCarousel from './components/HeroCarousel.vue'
import AboutSection from './components/AboutSection.vue'
import ContactSection from './components/ContactSection.vue'
import Footer from './components/Footer.vue'

import { getPosition } from "./services/geolocation";

// Variables pour stocker latitude et longitude
const latitude = ref<number | null>(null)
const longitude = ref<number | null>(null)

onMounted(() => {
  getPosition()
    .then(pos => {
      latitude.value = pos.coords.latitude
      longitude.value = pos.coords.longitude
      console.log("Latitude:", latitude.value, "Longitude:", longitude.value)
    })
    .catch(err => console.error("Erreur géolocalisation:", err))
})
</script>

<template>
  <div id="app" class="smooth-scroll">
    <router-view />
  </div>
</template>

<style>
html {
  scroll-behavior: smooth;
}
</style>
