<template>
  <div class="card">
    <div class="p-fluid">
      <h2>{{ isRegistering ? 'Skapa konto' : 'Logga in' }}</h2>

      <div class="field">
        <FloatLabel variant="on">
          <label for="email">Email</label>
          <InputText
            v-model="email"     
          />
        </FloatLabel>
      </div>

      <div class="field" v-if="isRegistering">
        <FloatLabel variant="on">
          <label for="firstName">Förnamn</label>
          <InputText
            v-model="firstName"
          />
        </FloatLabel>
      </div>

      <div class="field" v-if="isRegistering">
        <FloatLabel variant="on">
          <label for="lastName">Efternamn</label>
          <InputText
            v-model="lastName"
          />
        </FloatLabel>
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
            :feedback="false"
            placeholder="Lösenord"
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
import { FloatLabel } from 'primevue'

const { showInfoToast, showErrorToast } = useToaster()

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
      showInfoToast('Ett meddelande har skickats till admin för bekräftelse.')
    } catch (err) {
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
