<script setup lang="ts">
interface Props {
  variant?: 'primary' | 'secondary';
  size?: 'sm' | 'md' | 'lg';
  fullWidth?: boolean;
  disabled?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
  fullWidth: false,
  disabled: false,
});
</script>

<template>
  <button
    :disabled="disabled"
    :class="[
      'font-semibold transition-all duration-200 focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed',
      variant === 'primary' ? 'bg-[#7A1F1F] text-white hover:bg-[#6A1A1A]' : 'bg-white text-[#7A1F1F] hover:bg-gray-100',
      size === 'sm' ? 'px-4 py-2 text-sm rounded-full' : size === 'lg' ? 'px-8 py-4 text-lg rounded-full' : 'px-6 py-3 text-base rounded-full',
      fullWidth ? 'w-full' : '',
    ]"
  >
    <slot />
  </button>
</template>

