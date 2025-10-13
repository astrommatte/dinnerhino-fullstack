<template>
  <template>
  <div class="test-box p-4">
    <h1>Testtext</h1>
    <p>Detta är ett test för att se dark mode.</p>
    <Button label="Testknapp" />
  </div>
</template>
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
    background-color: rgba(255, 255, 255, 0.6);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
  }

  .p-toast{
    max-width: 300px;
  }

  .p-button{
    margin: 5px;
  }
  .test-box {
  background-color: var(--surface-card);
  color: var(--text-color);
}

</style>
