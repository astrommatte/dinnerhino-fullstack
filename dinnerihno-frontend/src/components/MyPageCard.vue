<template>
  <form @submit.prevent="updateUser">
    <div class="field">
      <label for="firstName">Förnamn</label>
      <InputText id="firstName" v-model="user.firstName" placeholder="Förnamn" />
    </div>

    <div class="field">
      <label for="lastName">Efternamn</label>
      <InputText id="lastName" v-model="user.lastName" placeholder="Efternamn" />
    </div>

    <div class="field">
      <label for="email">E-post</label>
      <InputText id="email" v-model="user.email" placeholder="E-post" />
    </div>

    <div class="field">
      <label for="password">Nytt lösenord (valfritt)</label>
      <Password id="password" v-model="user.password" toggleMask placeholder="Lösenord" />
    </div>

    <div class="update-button">
      <Button icon="pi pi-user-edit" type="submit" label="Uppdatera" />
    </div>
  </form>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import Button from 'primevue/button'
import { useAuthStore } from '@/stores/useAuthStore'

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const authStore = useAuthStore()
const currentUser = computed(() => authStore.user)

// Lokalt redigerbar användare
const user = ref({
  firstName: '',
  lastName: '',
  email: '',
  password: ''
})

// Fyll i befintliga användardata vid mount
onMounted(() => {
  if (currentUser.value) {
    user.value.firstName = currentUser.value.firstName
    user.value.lastName = currentUser.value.lastName
    user.value.email = currentUser.value.email
  }
})

// Uppdatera användaren
const updateUser = async () => {
  const headers = { Authorization: localStorage.getItem('auth') }

  // Skapa payload utan lösenord om det är tomt
  const payload = {
    firstName: user.value.firstName,
    lastName: user.value.lastName,
    email: user.value.email
  }

  if (user.value.password && user.value.password.length > 0) {
    payload.password = user.value.password
  }

  try {
    const response = await axios.put(`${apiUrl}/api/users/my-page/${currentUser.value.id}`, payload, { headers })
    alert('Användare uppdaterad!')
    authStore.setUser(response.data) // Om du har en metod i din store
    user.value.password = '' // Töm lösenordet lokalt efter update
  } catch (error) {
    console.error(error)
    alert('Misslyckades att uppdatera användaren.')
  }
}
</script>

<style scoped>
.field {
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
}

.update-button {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}
</style>
