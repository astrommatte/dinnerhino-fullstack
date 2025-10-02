<template>
  <header>
    <div class="wrapper">
      <nav>
        <span v-if="authStore.user" class="pi pi-user">{{ authStore.user.firstName }}</span>
        <span v-else>Ej inloggad</span>
        <RouterLink to="/recipes">Recept</RouterLink>
        <RouterLink to="/myrecipes">Mina Recept</RouterLink>
        <RouterLink to="/shoppinglist">Handlingslista</RouterLink>
        <RouterLink to="/admin" v-if="authStore.user.role === 'ADMIN'" >Admin</RouterLink>
        <Button icon="pi pi-sign-out" @click="handleLogout" severity="danger" />
      </nav>
    </div>
  </header>
</template>

<script setup>
  import { RouterLink } from 'vue-router'
  import { useAuthStore } from '@/stores/useAuthStore';
  import { useRouter } from 'vue-router';

  const authStore = useAuthStore()
  const router = useRouter()

  const handleLogout = () => {
    authStore.logout()
    router.push('/login')
  }
</script>

<style>
  header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    background: rgb(218, 203, 203);
    z-index: 1000;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }

  .wrapper {
    max-width: 1200px;
    margin: 0 auto;
    padding: 1rem;
  }

  nav {
    display: flex;
    gap: 1rem;
  }
</style>
