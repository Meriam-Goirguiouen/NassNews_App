<template>
  <section id="home" class="bg-[#F4EDE4] min-h-[calc(100vh-4rem)] flex flex-col items-center justify-start px-4 md:px-6 lg:px-8 pt-20 md:pt-24">
    <div class="max-w-7xl w-full flex flex-col md:flex-row items-start justify-between gap-2 mx-auto">
      <!-- Left: Hero Card -->
      <div class="flex flex-col w-full md:w-[415px] min-w-[325px]">
        <div class="bg-[#7A1F1F] rounded-3xl shadow-lg px-8 pt-9 pb-5 w-full h-[400px] md:h-[500px] lg:h-[600px] flex flex-col justify-center">
          <h1 class="text-4xl md:text-5xl font-bold text-white mb-4 leading-tight">
            NassNews Best News 2025
          </h1>
          <p class="text-base md:text-lg text-white/90 mb-6">
            Embark on the ultimate test of endurance and determination with NassNews, where each headline is a journey of daily discovery.
          </p>
          <a href="#about" class="inline-flex items-center gap-1 px-5 py-2 rounded-full bg-white text-[#7A1F1F] font-semibold text-base shadow hover:bg-[#E6E6E6] transition mb-0">
            Register Here
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M13 7l5 5m0 0l-5 5m5-5H6" />
            </svg>
          </a>
        </div>
      </div>

      <!-- Right: Photo Carousel -->
      <div class="flex-1 flex flex-col items-center w-full md:w-auto">
        <div class="flex flex-col items-center w-full mb-2">
          <div class="relative w-full h-[400px] md:h-[500px] lg:h-[600px] rounded-3xl shadow-md overflow-hidden">
            <transition name="fade" mode="out-in">
              <img
                :key="currentSlide"
                :src="slides[currentSlide].image"
                :alt="slides[currentSlide].title"
                class="w-full h-full object-cover object-center"
              />
            </transition>
          </div>
          <div class="flex justify-center gap-2 mt-3">
            <button
              v-for="(_slide, idx) in slides"
              :key="idx"
              @click="currentSlide = idx"
              :class="[
                'rounded-full transition-all duration-300',
                currentSlide === idx ? 'bg-[#D4AF37] w-8 h-3' : 'bg-[#E6E6E6] w-3 h-3 hover:bg-[#A3B18A]'
              ]"
            ></button>
          </div>
        </div>
      </div>
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
    description: 'Stay informed about the most important events happening in your city. From political developments to cultural celebrations, NassNews brings you comprehensive coverage of everything that matters in Morocco. Our dedicated team of journalists works around the clock to ensure you never miss a story.',
    image: 'https://plus.unsplash.com/premium_photo-1673415819362-c2ca640bfafe?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1171'
  },
  {
    title: 'Community Updates',
    description: 'Discover local announcements and cultural activities near you. Whether it\'s a new community center opening, a local festival, or important neighborhood news, we keep you connected to what\'s happening in your area. Join thousands of readers who rely on NassNews for their daily dose of local information.',
    image: 'https://images.pexels.com/photos/1167355/pexels-photo-1167355.jpeg?auto=compress&cs=tinysrgb&w=1920'
  },
  {
    title: 'Real-Time Coverage',
    description: 'Get breaking news as it happens, all in one place. Our real-time news feed ensures you\'re always up-to-date with the latest developments. From emergency alerts to major announcements, NassNews delivers instant updates to keep you informed and prepared.',
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
/* Fade transition for carousel images and news panel */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease-in-out;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
