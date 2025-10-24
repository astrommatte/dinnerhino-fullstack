<template>
  <header class="navbar">
    <div class="wrapper">
      <!-- Hamburger-knapp endast på små skärmar -->
      <Button 
        icon="pi pi-bars" 
        class="hamburger-button" 
        @click="isMenuVisible = true" 
      />

      <!-- Vanlig meny för större skärmar -->
      <nav class="nav-desktop">
        <span v-if="authStore.user" class="pi pi-user"> Inloggad som: {{ authStore.user.firstName }}</span>
        <RouterLink to="/mypage">Mina sidor</RouterLink>
        <RouterLink to="/myrecipes">Mina Recept</RouterLink>
        <RouterLink to="/recipes">Swipea Recept</RouterLink>
        <RouterLink to="/shoppinglist">Handlingslista</RouterLink>
        <RouterLink to="/admin" v-if="isAdmin">Admin</RouterLink>
        <Button icon="pi pi-question-circle" severity="secondary" @click="openInfoModal" />
        <Button icon="pi pi-sign-out" label="Logga ut" @click="handleLogout" />
      </nav>
      
    </div>

    <!-- PrimeVue Drawer för mobilmeny -->
    <Drawer v-model:visible="isMenuVisible" position="left" class="mobile-menu">
      <span v-if="authStore.user" class="pi pi-user"> Inloggad som: {{ authStore.user.firstName }}</span>
      <div class="mobile-links">
        <RouterLink to="/mypage" @click="isMenuVisible = false">Mina sidor</RouterLink>
        <RouterLink to="/myrecipes" @click="isMenuVisible = false">Mina Recept</RouterLink>
        <RouterLink to="/recipes" @click="isMenuVisible = false">Swipea Recept</RouterLink>
        <RouterLink to="/shoppinglist" @click="isMenuVisible = false">Handlingslista</RouterLink>
        <RouterLink to="/admin" v-if="isAdmin" @click="isMenuVisible = false">Admin</RouterLink>
        <Button icon="pi pi-question-circle" severity="secondary" @click="openInfoModal" />
        <Button icon="pi pi-sign-out" label="Logga ut" @click="handleLogout" />
      </div>
    </Drawer>
    
  </header>
      <Dialog header="Information om appen" v-model:visible="infoModal" modal closeOnEscape>
      <ul>
        <li>Denna app ska vara till hjälp om man har svårt att komma på vad man ska äta!</li>
        <li>Man kan skapa sina egna recept i "Mina recept", när du gjort det så kommer 
          receptet dyka upp för andra i "swipe-delen"
        </li>
        <li>Man swipear helt enkelt på recept som dyker upp(i swipe-delen), sen följer ingredienserna med till en handlingslista</li>
        <li>Innan man swipear ett recept så kan man "trycka bort" en eller flera ingredienser</li>
        <li>Tex: ett recept dyker upp, Köttfärssås, du har redan lök så du stryker över det direkt,
          då kommer ingrediensen lök vara överstruken direkt i handlingslistan när du väl kommer dit</li>
        <li>I handlingslistan så kan du ladda hem receptets beskrivning samt ingredienser till en pdf-fil</li>
        <li>En beskrivning av ett recept är helt enkelt en utförlig text där man förklarar hur man tillagar maten.
          (Om man vill göra det dvs, absolut inget måste!)
        </li>
        <li>Ingredienserna som blir överstrukna i swipe-delen, kommer inte komma med i pdf-filen</li>
      </ul>
    </Dialog>
</template>

<script setup>
  import { ref } from 'vue';
  import { RouterLink } from 'vue-router'
  import { useAuthStore } from '@/stores/useAuthStore';
  import { useRouter } from 'vue-router';
  import { computed } from 'vue';
  import Drawer from 'primevue/drawer'

  const authStore = useAuthStore()
  const router = useRouter()
  const isAdmin = computed(() => authStore.user?.role === 'ADMIN')
  const isMenuVisible = ref(false)
  const infoModal = ref(false)

  const handleLogout = () => {
    authStore.logout()
    router.push('/login')
  }

  const openInfoModal = (() => {
    infoModal.value = true
    isMenuVisible.value = false
  })
</script>

<style scoped>
header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.navbar {
  background-color: var(--surface-section);
  padding: 1rem;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  position: fixed;
  width: 100%;
  top: 0;
  z-index: 1000;
}

.wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Visa navbar-länkar bara på större skärmar */
.nav-desktop {
  display: flex;
  gap: 1rem;
}

.hamburger-button {
  display: none;
}

/* Visa hamburgarikonen bara på små skärmar */
@media (max-width: 1024px) {
  .nav-desktop {
    display: none;
  }
  .hamburger-button {
    display: inline-flex;
  }
}

/* Du kan styla sidebar också */
.mobile-links {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1rem;
}
</style>

