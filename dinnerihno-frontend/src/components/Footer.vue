<template>
  <footer class="footer">
    <p>
      © 2025 Mattias. Skicka gärna feedback på
      <a :href="`mailto:${email}`">{{ email }}</a> eller
      <a :href="githubUrl" target="_blank" rel="noopener noreferrer">GitHub</a>.
    </p>
  </footer>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  email: {
    type: String,
    default: 'astrom.matte@gmail.com'
  },
  githubUrl: {
    type: String,
    default: 'https://github.com/ditt-anvandarnamn'
  }
})
</script>

<style scoped>
    .footer {
        padding: 1rem;
        text-align: center;
        background-color: var(--surface-footer);
        font-size: 0.9rem;
    }
    .footer a {
        color: #007acc;
        text-decoration: none;
    }
    .footer a:hover {
        text-decoration: underline;
    }
</style>