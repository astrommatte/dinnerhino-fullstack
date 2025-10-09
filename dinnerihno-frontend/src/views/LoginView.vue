<template>
  <div class="card">
    <div class="p-fluid">
      <h2>{{ isRegistering ? 'Skapa konto' : 'Logga in' }}</h2>

      <div class="field">
        <InputText
          v-model="email"
          placeholder="E-post"
        />
      </div>

      <div class="field" v-if="isRegistering">
        <InputText
          v-model="firstName"
          placeholder="Förnamn"
        />
      </div>

      <div class="field" v-if="isRegistering">
        <InputText
          v-model="lastName"
          placeholder="Efternamn"
        />
      </div>

      <div class="field">
        <Password
          v-if="isRegistering"
          v-model="password"
          toggleMask
          placeholder="Lösenord"
        />
          <Password
          v-else
          v-model="password"
          toggleMask
          placeholder="Lösenord"
          :feedback="false"
        />
      </div>

      <div class="login-view-buttons">
        <Button @click="handleSubmit">{{ isRegistering ? 'Skapa konto' : 'Logga in' }}</Button>
        <Button
          class="toggle-button"
          @click="toggleMode"
        >
          {{ isRegistering ? 'Har du redan ett konto?' : 'Skapa ny användare' }}
        </Button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import Button from 'primevue/button'
import axios from 'axios'
import { useAuthStore } from '@/stores/useAuthStore'
import { hideLoading, showLoading } from '@/stores/useLoadingStore'
import { useToaster } from '@/stores/useToastStore'

const { showSuccessToast, showErrorToast } = useToaster()

const isRegistering = ref(false)
const authStore = useAuthStore()

const firstName = ref('')
const lastName = ref('')
const email = ref('')
const password = ref('')

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const toggleMode = () => {
  isRegistering.value = !isRegistering.value
  resetFields()
}

const resetFields = () => {
  firstName.value = ''
  lastName.value = ''
  email.value = ''
  password.value = ''
}

const handleSubmit = async () => {
  if (isRegistering.value) {
    try {
      showLoading()
      await axios.post(`${apiUrl}/api/users/create`, {
        firstName: firstName.value,
        lastName: lastName.value,
        email: email.value,
        password: password.value
      })
      resetFields()
      toggleMode()
      showSuccessToast('Användare skapad!')
    } catch (err) {
      console.error(err)
      showErrorToast('Misslyckades att skapa användare')
    } finally {
      hideLoading()
    }
  } else {
    try {
      showLoading()
      await authStore.login({
        email: email.value,
        password: password.value
      })

    } catch (err) {
      
    } finally{
      hideLoading()
    }
  }
}
</script>

<style scoped>
.card {
  max-width: 400px;
  margin: 2rem auto;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 0 10px #ccc;
}

.p-fluid {
  justify-content: center;
  align-items: center;
  text-align: center;
}

input {
  min-width: 234px;
}

.field {
  margin-bottom: 1rem;
  display: flex;
  justify-content: center;
}

.login-view-buttons {
  display: flex;
  justify-content: space-between;
  margin-left: 50px;
  margin-right: 50px;
}

button {
  font-size: 12px;
}
</style>
