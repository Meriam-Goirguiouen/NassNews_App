<template>
  <section id="home" class="bg-white min-h-screen pt-20">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Featured Hero Story -->
      <div class="mb-12">
        <div class="relative h-[calc(100vh-8rem)] min-h-[600px] max-h-[800px] rounded-2xl overflow-hidden shadow-2xl group cursor-pointer">
          <transition name="fade" mode="out-in">
            <div
              :key="currentSlide"
              class="absolute inset-0"
            >
              <img
                :src="slides[currentSlide].image"
                :alt="slides[currentSlide].title"
                class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105"
              />
              <!-- Gradient Overlay -->
              <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent"></div>
              
              <!-- Content Overlay -->
              <div class="absolute bottom-0 left-0 right-0 p-8 md:p-12 lg:p-16">
                <div class="max-w-3xl">
                  <div class="inline-block mb-4">
                    <span class="bg-[#7A1F1F] text-white px-4 py-2 rounded-full text-sm font-semibold">
                      {{ slides[currentSlide].category }}
                    </span>
                  </div>
                  <h1 class="text-3xl md:text-5xl lg:text-6xl font-bold text-white mb-4 leading-tight">
                    {{ slides[currentSlide].title }}
                  </h1>
                  <p class="text-lg md:text-xl text-white/90 mb-6 line-clamp-2">
                    {{ slides[currentSlide].description }}
                  </p>
                  <div class="flex items-center gap-4 text-white/80 text-sm">
                    <span>{{ slides[currentSlide].date }}</span>
                    <span>•</span>
                    <span>{{ slides[currentSlide].author }}</span>
                  </div>
                </div>
              </div>
            </div>
          </transition>
          
          <!-- Navigation Dots -->
          <div class="absolute bottom-6 left-1/2 transform -translate-x-1/2 flex gap-2 z-10">
            <button
              v-for="(_slide, idx) in slides"
              :key="idx"
              @click="currentSlide = idx"
              :class="[
                'rounded-full transition-all duration-300',
                currentSlide === idx ? 'bg-[#D4AF37] w-10 h-2' : 'bg-white/50 w-2 h-2 hover:bg-white/80'
              ]"
            ></button>
          </div>
          
          <!-- Navigation Arrows -->
          <button
            @click="previousSlide"
            class="absolute left-4 top-1/2 transform -translate-y-1/2 bg-white/20 hover:bg-white/30 backdrop-blur-sm rounded-full p-3 transition-all z-10"
          >
            <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <button
            @click="nextSlide"
            class="absolute right-4 top-1/2 transform -translate-y-1/2 bg-white/20 hover:bg-white/30 backdrop-blur-sm rounded-full p-3 transition-all z-10"
          >
            <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Category Tabs -->
      <div class="mb-8">
        <div class="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-hide">
          <button
            v-for="category in categories"
            :key="category"
            @click="selectedCategory = category"
            :class="[
              'px-6 py-2.5 rounded-full font-semibold text-sm whitespace-nowrap transition-all',
              selectedCategory === category
                ? 'bg-[#7A1F1F] text-white shadow-lg'
                : 'bg-[#F4EDE4] text-[#5F6B61] hover:bg-[#E6E6E6]'
            ]"
          >
            {{ category }}
          </button>
        </div>
      </div>

      <!-- News Grid -->
      <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
        <article
          v-for="(news, idx) in filteredNews"
          :key="idx"
          class="bg-white rounded-xl overflow-hidden shadow-md hover:shadow-xl transition-all duration-300 cursor-pointer group border border-gray-100"
        >
          <div class="relative h-48 overflow-hidden">
            <img
              :src="news.image"
              :alt="news.title"
              class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110"
            />
            <div class="absolute top-4 right-4">
              <span class="bg-[#7A1F1F] text-white px-3 py-1 rounded-full text-xs font-semibold">
                {{ news.category }}
              </span>
            </div>
          </div>
          <div class="p-6">
            <h3 class="text-xl font-bold text-gray-900 mb-3 line-clamp-2 group-hover:text-[#7A1F1F] transition-colors">
              {{ news.title }}
            </h3>
            <p class="text-gray-600 text-sm mb-4 line-clamp-2">
              {{ news.description }}
            </p>
            <div class="flex items-center justify-between text-xs text-gray-500">
              <span>{{ news.date }}</span>
              <span class="flex items-center gap-1 text-[#7A1F1F] font-semibold">
                Read More
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                </svg>
              </span>
            </div>
          </div>
        </article>
      </div>

      <!-- Breaking News Banner -->
      <div class="bg-[#7A1F1F] rounded-xl p-6 mb-12 flex items-center gap-4">
        <div class="flex-shrink-0">
          <div class="bg-white/20 rounded-full p-3">
            <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
        </div>
        <div class="flex-1">
          <p class="text-white font-semibold text-lg">Breaking News</p>
          <p class="text-white/90 text-sm">Stay updated with the latest developments from Morocco</p>
        </div>
        <button class="bg-[#D4AF37] hover:bg-[#D4AF37]/90 text-white px-6 py-2 rounded-full font-semibold transition-colors">
          Subscribe
        </button>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'

const currentSlide = ref(0)
const selectedCategory = ref('All')
let intervalId: number | null = null

const categories = ['All', 'Politics', 'Culture', 'Sports', 'Economy', 'Technology', 'Local']

const slides = [
  {
    title: 'Latest News from Morocco',
    description: 'Stay informed about the most important events happening in your city. From political developments to cultural celebrations, NassNews brings you comprehensive coverage of everything that matters in Morocco.',
    image: 'https://images.unsplash.com/photo-1504711434969-e33886168f5c?ixlib=rb-4.1.0&auto=format&fit=crop&q=80&w=1920',
    category: 'Politics',
    date: '2 hours ago',
    author: 'NassNews Editorial'
  },
  {
    title: 'Community Updates',
    description: 'Discover local announcements and cultural activities near you. Whether it\'s a new community center opening, a local festival, or important neighborhood news, we keep you connected.',
    image: 'https://images.unsplash.com/photo-1585829365295-ab7cd400c167?ixlib=rb-4.1.0&auto=format&fit=crop&q=80&w=1920',
    category: 'Local',
    date: '5 hours ago',
    author: 'NassNews Editorial'
  },
  {
    title: 'Real-Time Coverage',
    description: 'Get breaking news as it happens, all in one place. Our real-time news feed ensures you\'re always up-to-date with the latest developments from emergency alerts to major announcements.',
    image: 'https://images.unsplash.com/photo-1499750310107-5fef28a66643?ixlib=rb-4.1.0&auto=format&fit=crop&q=80&w=1920',
    category: 'Breaking',
    date: '1 hour ago',
    author: 'NassNews Editorial'
  }
]

const newsItems = [
  {
    title: 'Moroccan Economy Shows Strong Growth in Q4',
    description: 'Latest economic indicators reveal positive trends across multiple sectors, signaling robust recovery.',
    image: 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?ixlib=rb-4.1.0&auto=format&fit=crop&q=80&w=800',
    category: 'Economy',
    date: '3 hours ago'
  },
  {
    title: 'Cultural Festival Celebrates Moroccan Heritage',
    description: 'Annual event brings together artists, musicians, and performers to showcase rich cultural traditions.',
    image: 'https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?ixlib=rb-4.1.0&auto=format&fit=crop&q=80&w=800',
    category: 'Culture',
    date: '6 hours ago'
  },
  {
    title: 'Local Football Team Wins Championship',
    description: 'Historic victory marks a new era for Moroccan sports with record-breaking performance.',
    image: 'https://images.unsplash.com/photo-1574629810360-7efbbe195018?ixlib=rb-4.1.0&auto=format&fit=crop&q=80&w=800',
    category: 'Sports',
    date: '1 day ago'
  },
  {
    title: 'Tech Innovation Hub Opens in Casablanca',
    description: 'New technology center aims to foster innovation and support local startups in the digital economy.',
    image: 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?ixlib=rb-4.1.0&auto=format&fit=crop&q=80&w=800',
    category: 'Technology',
    date: '2 days ago'
  },
  {
    title: 'City Council Announces New Infrastructure Projects',
    description: 'Major investments planned for transportation and public facilities to improve urban living.',
    image: 'https://images.unsplash.com/photo-1449824913935-59a10b8d2000?ixlib=rb-4.1.0&auto=format&fit=crop&q=80&w=800',
    category: 'Local',
    date: '4 hours ago'
  },
  {
    title: 'Parliament Debates New Education Reform Bill',
    description: 'Proposed legislation aims to modernize curriculum and improve access to quality education nationwide.',
    image: 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca?ixlib=rb-4.1.0&auto=format&fit=crop&q=80&w=800',
    category: 'Politics',
    date: '5 hours ago'
  }
]

const filteredNews = computed(() => {
  if (selectedCategory.value === 'All') {
    return newsItems
  }
  return newsItems.filter(item => item.category === selectedCategory.value)
})

const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % slides.length
}

const previousSlide = () => {
  currentSlide.value = currentSlide.value === 0 ? slides.length - 1 : currentSlide.value - 1
}

onMounted(() => {
  intervalId = window.setInterval(nextSlide, 6000)
})

onUnmounted(() => {
  if (intervalId) {
    clearInterval(intervalId)
  }
})
</script>

<style scoped>
/* Fade transition for carousel images */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.6s ease-in-out;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Line clamp utilities */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Hide scrollbar for category tabs */
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
</style>
