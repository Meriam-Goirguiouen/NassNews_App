<template>
  <section id="contact" class="py-20 bg-white">
    <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-12 fade-in-scroll">
        <h2 class="text-4xl font-bold mb-4 text-gray-900">Contact Us</h2>
        <p class="text-lg text-gray-600">
          Have questions or suggestions? We'd love to hear from you.
        </p>
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-6 fade-in-scroll delay-200">
        <div>
          <label for="name" class="block text-sm font-medium text-gray-700 mb-2">
            Name
          </label>
          <input
            v-model="form.name"
            type="text"
            id="name"
            required
            class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-moroccan-red focus:border-transparent transition-all duration-200 outline-none"
            placeholder="Your full name"
          />
        </div>

        <div>
          <label for="email" class="block text-sm font-medium text-gray-700 mb-2">
            Email
          </label>
          <input
            v-model="form.email"
            type="email"
            id="email"
            required
            class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-moroccan-red focus:border-transparent transition-all duration-200 outline-none"
            placeholder="your.email@example.com"
          />
        </div>

        <div>
          <label for="message" class="block text-sm font-medium text-gray-700 mb-2">
            Message
          </label>
          <textarea
            v-model="form.message"
            id="message"
            required
            rows="6"
            class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-moroccan-red focus:border-transparent transition-all duration-200 outline-none resize-none"
            placeholder="Tell us what's on your mind..."
          ></textarea>
        </div>

        <div>
          <button
            type="submit"
            :disabled="isSubmitting"
            :class="[
              'w-full py-3 px-6 rounded-lg font-medium text-white transition-all duration-300 transform',
              isSubmitting
                ? 'bg-gray-400 cursor-not-allowed'
                : 'bg-moroccan-red hover:bg-moroccan-green hover:shadow-lg hover:scale-[1.02]'
            ]"
          >
            {{ isSubmitting ? 'Sending...' : 'Send Message' }}
          </button>
        </div>

        <div
          v-if="successMessage"
          class="p-4 bg-green-50 border border-green-200 rounded-lg text-green-800 text-center"
        >
          {{ successMessage }}
        </div>
      </form>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const form = ref({
  name: '',
  email: '',
  message: ''
})

const isSubmitting = ref(false)
const successMessage = ref('')

const handleSubmit = async () => {
  isSubmitting.value = true

  await new Promise(resolve => setTimeout(resolve, 1000))

  successMessage.value = 'Thank you for your message! We\'ll get back to you soon.'

  form.value = {
    name: '',
    email: '',
    message: ''
  }

  isSubmitting.value = false

  setTimeout(() => {
    successMessage.value = ''
  }, 5000)
}

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
