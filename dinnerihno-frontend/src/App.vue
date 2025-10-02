<template>
  <Navbar v-if="!isLoginPage" />
  <router-view />
</template>

<script setup>
import { useRoute } from 'vue-router'
import Navbar from './components/Navbar.vue'
import { computed, onMounted } from 'vue'
import { useAuthStore } from './stores/useAuthStore'

const route = useRoute()
const isLoginPage = computed(() => route.path === '/login')

const authStore = useAuthStore()

onMounted(() => {
  if (authStore.token) {
    authStore.fetchUserProfile()
  }
})
</script>
