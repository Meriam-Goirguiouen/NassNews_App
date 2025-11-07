<template>
    <section id="home" class="relative h-screen overflow-hidden">
    <div
      v-for="(slide, index) in slides"
      :key="index"
      :class="[
        'absolute inset-0 transition-opacity duration-1000',
        currentSlide === index ? 'opacity-100' : 'opacity-0'
      ]"
    >
      <img
        :src="slide.image"
        :alt="slide.title"
        class="w-full h-full object-cover"
      />
      <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent"></div>

      <div class="absolute inset-0 flex items-center justify-center">
        <div class="text-center text-white px-4 max-w-4xl">
          <h2 class="text-4xl md:text-6xl font-bold mb-4 animate-fade-in">
            {{ slide.title }}
          </h2>
          <p class="text-lg md:text-xl mb-8 animate-fade-in-delay">
            {{ slide.description }}
          </p>
        </div>
      </div>
    </div>

    <div class="absolute bottom-8 left-0 right-0 flex justify-center space-x-2">
      <button
        v-for="(_slide, index) in slides"
        :key="index"
        @click="currentSlide = index"
        :class="[
          'w-3 h-3 rounded-full transition-all duration-300',
          currentSlide === index ? 'bg-moroccan-red w-8' : 'bg-white/50 hover:bg-white/80'
        ]"
      ></button>
    </div>

    <div class="absolute bottom-24 left-0 right-0 flex justify-center">
      <a
        href="#about"
        class="bg-moroccan-red text-white px-8 py-3 rounded-full font-medium hover:bg-moroccan-green transition-colors duration-300 shadow-lg hover:shadow-xl transform hover:scale-105"
      >
        See More News
      </a>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const currentSlide = ref(0)
let intervalId: number | null = null

const slides = [
  {
    title: 'Latest News from Morocco',
    description: 'Stay informed about the most important events happening in your city',
    image: 'https://images.pexels.com/photos/3246665/pexels-photo-3246665.jpeg?auto=compress&cs=tinysrgb&w=1920'
  },
  {
    title: 'Community Updates',
    description: 'Discover local announcements and cultural activities near you',
    image: 'https://images.pexels.com/photos/1167355/pexels-photo-1167355.jpeg?auto=compress&cs=tinysrgb&w=1920'
  },
  {
    title: 'Real-Time Coverage',
    description: 'Get breaking news as it happens, all in one place',
    image: 'https://images.pexels.com/photos/1090638/pexels-photo-1090638.jpeg?auto=compress&cs=tinysrgb&w=1920'
  }
]

const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % slides.length
}

onMounted(() => {
  intervalId = window.setInterval(nextSlide, 5000)
})

onUnmounted(() => {
  if (intervalId) {
    clearInterval(intervalId)
  }
})
</script>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fadeIn 0.8s ease-out;
}

.animate-fade-in-delay {
  animation: fadeIn 0.8s ease-out 0.2s both;
}
</style>
