<template>
  <section id="about" class="py-20 bg-[#F4EDE4]">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-16 fade-in-scroll">
        <h2 class="text-4xl md:text-5xl font-bold mb-4 text-[#7A1F1F]">
          About NassNews
        </h2>
        <p class="text-xl text-[#5F6B61] max-w-2xl mx-auto">
          Your trusted source for local Moroccan news
        </p>
      </div>

      <div class="grid md:grid-cols-2 gap-12 items-center mb-16">
        <div class="fade-in-scroll">
          <div class="bg-white rounded-2xl p-8 shadow-lg">
            <h3 class="text-2xl font-bold mb-4 text-[#7A1F1F]">
              Centralized News Platform
            </h3>
            <p class="text-lg text-[#5F6B61] leading-relaxed mb-6">
              NassNews centralizes local Moroccan news from multiple sources into one intuitive platform.
              It helps citizens stay informed about their city's latest events, public announcements, and cultural activities.
            </p>
            <p class="text-[#5F6B61] leading-relaxed mb-6">
              Our mission is to bridge the gap between information and accessibility, ensuring that every Moroccan
              has easy access to the news that matters most to their community.
            </p>
            <div class="flex flex-wrap gap-4">
              <div class="flex items-center gap-3 bg-[#F4EDE4] px-4 py-2 rounded-lg">
                <div class="w-10 h-10 bg-[#7A1F1F] rounded-full flex items-center justify-center">
                  <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z" />
                  </svg>
                </div>
                <div>
                  <p class="font-semibold text-[#7A1F1F]">Trusted Source</p>
                  <p class="text-sm text-[#5F6B61]">Verified news</p>
                </div>
              </div>
              <div class="flex items-center gap-3 bg-[#F4EDE4] px-4 py-2 rounded-lg">
                <div class="w-10 h-10 bg-[#7A1F1F] rounded-full flex items-center justify-center">
                  <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <div>
                  <p class="font-semibold text-[#7A1F1F]">Real-Time</p>
                  <p class="text-sm text-[#5F6B61]">Live updates</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="fade-in-scroll delay-200">
          <div class="relative">
            <img
              src="https://images.unsplash.com/photo-1504711434969-e33886168f5c?ixlib=rb-4.1.0&auto=format&fit=crop&q=80&w=800"
              alt="Morocco news"
              class="rounded-2xl shadow-2xl"
            />
            <div class="absolute -bottom-6 -right-6 w-48 h-48 bg-[#A3B18A] opacity-20 rounded-2xl -z-10"></div>
            <div class="absolute -top-6 -left-6 w-48 h-48 bg-[#7A1F1F] opacity-20 rounded-2xl -z-10"></div>
          </div>
        </div>
      </div>

      <!-- Stats Section -->
      <div class="grid grid-cols-2 md:grid-cols-4 gap-6">
        <div class="bg-white rounded-xl p-6 text-center shadow-md fade-in-scroll">
          <div class="text-3xl font-bold text-[#7A1F1F] mb-2">50K+</div>
          <div class="text-[#5F6B61]">Active Readers</div>
        </div>
        <div class="bg-white rounded-xl p-6 text-center shadow-md fade-in-scroll delay-200">
          <div class="text-3xl font-bold text-[#7A1F1F] mb-2">100+</div>
          <div class="text-[#5F6B61]">Cities Covered</div>
        </div>
        <div class="bg-white rounded-xl p-6 text-center shadow-md fade-in-scroll delay-300">
          <div class="text-3xl font-bold text-[#7A1F1F] mb-2">1K+</div>
          <div class="text-[#5F6B61]">Daily Articles</div>
        </div>
        <div class="bg-white rounded-xl p-6 text-center shadow-md fade-in-scroll delay-400">
          <div class="text-3xl font-bold text-[#7A1F1F] mb-2">24/7</div>
          <div class="text-[#5F6B61]">News Coverage</div>
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
