<template>
  <form @submit.prevent="updateUser">
    <div class="field">
      <FloatLabel variant="on">
        <label for="firstName">Förnamn</label>
        <InputText id="firstName" v-model="user.firstName"/>
      </FloatLabel>
    </div>

    <div class="field">
      <FloatLabel variant="on">
        <label for="lastName">Efternamn</label>
        <InputText id="lastName" v-model="user.lastName" autocomplete="true"/>
      </FloatLabel>
    </div>

    <div class="field">
      <FloatLabel variant="on">
        <label for="username">Användarnamn</label>
        <InputText id="username" v-model="user.username" />
      </FloatLabel>
    </div>

    <div class="field">
        <Password id="password" v-model="user.password" toggleMask placeholder="Lösenord"/>
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
import { FloatLabel } from 'primevue'
import { useToaster } from '@/stores/useToastStore'
import { showLoading, hideLoading } from '@/stores/useLoadingStore'

const { showInfoToast, showSuccessToast, showErrorToast } = useToaster()
const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const authStore = useAuthStore()
const currentUser = computed(() => authStore.user)

// Lokalt redigerbar användare
const user = ref({
  firstName: '',
  lastName: '',
  username: '',
  password: ''
})

// Fyll i befintliga användardata vid mount
onMounted(() => {
  if (currentUser.value) {
    user.value.firstName = currentUser.value.firstName
    user.value.lastName = currentUser.value.lastName
    user.value.username = currentUser.value.username
  }
})

// Uppdatera användaren
const updateUser = async () => {
  const headers = { Authorization: localStorage.getItem('auth') }

  // Skapa payload utan lösenord om det är tomt
  const payload = {
    firstName: user.value.firstName,
    lastName: user.value.lastName,
    username: user.value.username
  }

  if (user.value.password && user.value.password.length > 0) {
    payload.password = user.value.password
  }

  try {
    showLoading()
    const response = await axios.put(`${apiUrl}/api/users/my-page/${currentUser.value.id}`, payload, { headers })
    showSuccessToast('Din profil är uppdaterad!')
    authStore.setUser(response.data) // Om du har en metod i din store
    user.value.password = '' // Töm lösenordet lokalt efter update
  } catch (error) {
    showErrorToast('Gick inte att uppdatera')
  } finally {
    hideLoading()
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

.card {
  display: flex;
  justify-content: center;
}

.p-inputtext,
.p-password,
.p-password-input {
  display: grid;
  width: 100%;
  box-sizing: border-box;
}
</style>
