<template>
  <section id="contact" class="bg-[#7A1F1F] py-20">
    <div class="max-w-6xl mx-auto px-4">
      <div class="text-center mb-14">
        <button class="px-5 py-2 rounded-full border border-white text-white font-medium mb-8 hover:bg-white/10 transition">Reach Out To Us</button>
        <h2 class="text-4xl md:text-5xl font-bold mb-4 text-white">We'd Love to Hear From You.</h2>
        <p class="text-lg text-white/90 mb-2">Or just reach out manually to <a href="mailto:hello@slothoi.com" class="underline hover:text-[#D4AF37] text-[#D4AF37]">hello@slothui.com</a>.</p>
      </div>
      <div class="grid md:grid-cols-3 gap-10 text-white">
        <!-- Email -->
        <div class="bg-transparent flex flex-col items-center text-center">
          <span class="mb-4 flex items-center justify-center h-14 w-14 rounded-full bg-[#D4AF37]/20">
            <!-- SVG Mail Icon -->
            <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" class="w-7 h-7 text-[#D4AF37]"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/></svg>
          </span>
          <div class="font-bold text-lg mb-1">Email Support</div>
          <div class="text-white/80 text-base mb-2">Our team can respond in real time.</div>
          <a class="text-[#D4AF37] hover:underline" href="mailto:hello@nassnews.com">hello@slothui.com</a>
        </div>
        <!-- Office -->
        <div class="bg-transparent flex flex-col items-center text-center">
          <span class="mb-4 flex items-center justify-center h-14 w-14 rounded-full bg-[#D4AF37]/20">
            <!-- SVG Office/Icon -->
            <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" class="w-7 h-7 text-[#D4AF37]"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/></svg>
          </span>
          <div class="font-bold text-lg mb-1">Visit Our Office</div>
          <div class="text-white/80 text-base mb-2">Visit our location in real life.</div>
          <div class="text-[#D4AF37]">221b Elementary Avenue, NY</div>
        </div>
        <!-- Call -->
        <div class="bg-transparent flex flex-col items-center text-center">
          <span class="mb-4 flex items-center justify-center h-14 w-14 rounded-full bg-[#D4AF37]/20">
            <!-- SVG Phone Icon -->
            <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" class="w-7 h-7 text-[#D4AF37]"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"/></svg>
          </span>
          <div class="font-bold text-lg mb-1">Call Us Directly</div>
          <div class="text-white/80 text-base mb-2">Available during working hours.</div>
          <a class="text-[#D4AF37] hover:underline" href="tel:+212784962153">(+212) 784962153</a>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
// No form/actions needed for this layout
</script>

<style scoped>
</style>
