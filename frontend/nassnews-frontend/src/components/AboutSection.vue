<template>
  <section id="about" class="py-20 bg-gray-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="grid md:grid-cols-2 gap-12 items-center">
        <div class="fade-in-scroll">
          <h2 class="text-4xl font-bold mb-4 text-gray-900">
            About NassNews
            <div class="h-1 w-20 bg-gradient-to-r from-moroccan-red to-moroccan-green mt-2"></div>
          </h2>
          <p class="text-lg text-gray-700 leading-relaxed mb-6">
            NassNews centralizes local Moroccan news from multiple sources into one intuitive platform.
            It helps citizens stay informed about their city's latest events, public announcements, and cultural activities.
          </p>
          <p class="text-gray-600 leading-relaxed">
            Our mission is to bridge the gap between information and accessibility, ensuring that every Moroccan
            has easy access to the news that matters most to their community.
          </p>
          <div class="flex items-center mt-6 space-x-4">
            <div class="w-12 h-12 bg-moroccan-red rounded-full flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z" />
              </svg>
            </div>
            <div>
              <p class="font-semibold text-gray-900">Trusted Source</p>
              <p class="text-sm text-gray-600">For local Moroccan news</p>
            </div>
          </div>
        </div>

        <div class="fade-in-scroll delay-200">
          <div class="relative">
            <img
              src="https://images.pexels.com/photos/1181406/pexels-photo-1181406.jpeg?auto=compress&cs=tinysrgb&w=800"
              alt="Morocco landscape"
              class="rounded-lg shadow-2xl"
            />
            <div class="absolute -bottom-6 -right-6 w-48 h-48 bg-moroccan-green opacity-20 rounded-lg -z-10"></div>
            <div class="absolute -top-6 -left-6 w-48 h-48 bg-moroccan-red opacity-20 rounded-lg -z-10"></div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('visible')
        }
      })
    },
    { threshold: 0.1 }
  )

  document.querySelectorAll('.fade-in-scroll').forEach((el) => {
    observer.observe(el)
  })
})
</script>

<style scoped>
.fade-in-scroll {
  opacity: 0;
  transform: translateY(30px);
  transition: opacity 0.8s ease-out, transform 0.8s ease-out;
}

.fade-in-scroll.delay-200 {
  transition-delay: 0.2s;
}

.fade-in-scroll.visible {
  opacity: 1;
  transform: translateY(0);
}
</style>
