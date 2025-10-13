<template>
  <Navbar v-if="!isLoginPage" />
  <Toast position="top-center" />
  <router-view />
  <div v-if="isLoading" class="overlay-spinner">
    <ProgressSpinner />
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import Navbar from './components/Navbar.vue'
import { computed, onMounted } from 'vue'
import { useAuthStore } from './stores/useAuthStore'
import { isLoading } from './stores/useLoadingStore'

const route = useRoute()
const isLoginPage = computed(() => route.path === '/login')

const authStore = useAuthStore()

onMounted(() => {
  if (authStore.token) {
    authStore.fetchUserProfile()
  }
})
</script>

<style>

  .overlay-spinner {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.3);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1100;
  }

  .p-toast{
    max-width: 300px;
  }

  @media (prefers-color-scheme: dark) {
    .overlay {
      background-color: rgba(255, 255, 255, 0.15);
    }
  }

</style>
