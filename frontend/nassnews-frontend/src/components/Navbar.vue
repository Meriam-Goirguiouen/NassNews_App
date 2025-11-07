<template>
  <nav
    :class="[
      'fixed top-0 left-0 right-0 z-50 transition-all duration-300',
      scrolled ? 'bg-white shadow-md' : 'bg-transparent'
    ]"
  >
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-center h-16">
        <div class="flex-shrink-0">
          <h1 :class="['text-2xl font-bold', scrolled ? 'text-moroccan-red' : 'text-white']">
            NassNews
          </h1>
        </div>

        <div class="hidden md:flex items-center space-x-8">
          <a
            v-for="link in navLinks"
            :key="link.name"
            :href="link.href"
            :class="[
              'text-sm font-medium transition-colors duration-200',
              scrolled ? 'text-gray-700 hover:text-moroccan-red' : 'text-white hover:text-moroccan-green'
            ]"
          >
            {{ link.name }}
          </a>
        </div>

        <button
          @click="toggleMenu"
          :class="['md:hidden', scrolled ? 'text-gray-700' : 'text-white']"
        >
          <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
      </div>
    </div>

    <div v-if="mobileMenuOpen" class="md:hidden bg-white shadow-lg">
      <div class="px-2 pt-2 pb-3 space-y-1">
        <a
          v-for="link in navLinks"
          :key="link.name"
          :href="link.href"
          class="block px-3 py-2 text-base font-medium text-gray-700 hover:text-moroccan-red hover:bg-gray-50 transition-colors duration-200"
        >
          {{ link.name }}
        </a>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const scrolled = ref(false)
const mobileMenuOpen = ref(false)

const navLinks = [
  { name: 'Home', href: '#home' },
  { name: 'About Us', href: '#about' },
  { name: 'Contact', href: '#contact' },
  { name: 'Sign Up', href: '#signup' },
  { name: 'Sign In', href: '#signin' }
]

const handleScroll = () => {
  scrolled.value = window.scrollY > 50
}

const toggleMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>
