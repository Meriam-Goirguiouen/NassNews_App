<template>
  <footer class="bg-gray-900 text-white py-12 fade-in-scroll">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="border-t border-gray-800 pt-8">
        <div class="flex flex-col md:flex-row justify-between items-center space-y-4 md:space-y-0">
          <div class="flex items-center space-x-2">
            <span class="text-gray-400">&copy; 2025 NassNews</span>
            <span class="text-gray-600">—</span>
            <span class="text-gray-400">All Rights Reserved</span>
          </div>

          <div class="flex items-center space-x-6">
            <a
              href="#privacy"
              class="text-gray-400 hover:text-moroccan-red transition-colors duration-200"
            >
              Privacy Policy
            </a>
            <span class="text-gray-700">|</span>
            <a
              href="#terms"
              class="text-gray-400 hover:text-moroccan-red transition-colors duration-200"
            >
              Terms of Service
            </a>
            <span class="text-gray-700">|</span>
            <a
              href="#contact"
              class="text-gray-400 hover:text-moroccan-red transition-colors duration-200"
            >
              Contact
            </a>
          </div>

          <div class="flex items-center space-x-2">
            <span class="text-2xl">🇲🇦</span>
          </div>
        </div>

        <div class="mt-6 text-center">
          <div class="h-1 w-full max-w-xs mx-auto bg-gradient-to-r from-moroccan-red to-moroccan-green rounded-full opacity-50"></div>
        </div>
      </div>
    </div>
  </footer>
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

.fade-in-scroll.visible {
  opacity: 1;
  transform: translateY(0);
}
</style>
