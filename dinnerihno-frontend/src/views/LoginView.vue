<template>
  <div class="card">
    <!-- <i class="pi pi-spin pi-spinner" style="font-size: 2rem"></i> -->
    <Form :validation-schema="schema" @submit="handleSubmit">
      <div class="p-fluid">
        <h2>{{ isRegistering ? 'Skapa konto' : 'Logga in' }}</h2>
        <div class="field">
          <Field name="email" v-slot="{ field }">
            <InputText
              id="email"
              v-bind="field"
              placeholder="E-post"
              :class="{ 'p-invalid': errors.email }"
            />
          </Field>
          <ErrorMessage name="email" class="p-error" />
        </div>

        <div class="field" v-if="isRegistering">
          <Field name="firstName" v-slot="{ field }">
            <InputText
              id="firstName"
              v-bind="field"
              placeholder="Förnamn"
              :class="{ 'p-invalid': errors.firstName }"
            />
          </Field>
          <ErrorMessage name="firstName" class="p-error" />
        </div>

        <div class="field" v-if="isRegistering">
          <Field name="lastName" v-slot="{ field }">
            <InputText
              id="lastName"
              v-bind="field"
              placeholder="Efternamn"
              :class="{ 'p-invalid': errors.lastName }"
            />
          </Field>
          <ErrorMessage name="lastName" class="p-error" />
        </div>

        <div class="field">
          <Field name="password" v-slot="{ field }">
            <div class="password-container">
              <Password
                v-if="!isRegistering"
                v-bind="field"
                toggleMask
                id="password"
                placeholder="Lösenord"
                :feedback="false"
              />
              <Password
                id="password"
                v-if="isRegistering"
                v-bind="field"
                toggleMask
                placeholder="Lösenord"
                :class="{ 'p-invalid': errors.password }"
              />
            </div>
          </Field>
          <ErrorMessage name="password" class="p-error" />
        </div>
      </div>
        <div class="login-view-buttons">
          <Button type="submit">{{ isRegistering ? 'Skapa konto' : 'Logga in' }}</Button>
          <Button
            class="toggle-button"
            @click="toggleMode"
          >
            {{ isRegistering ? 'Har du redan ett konto?' : 'Skapa ny användare' }}
          </Button>
      </div>
    </Form>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Form, Field, ErrorMessage, useForm } from 'vee-validate'
import * as yup from 'yup'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import Button from 'primevue/button'
import axios from 'axios'
import { useAuthStore } from '@/stores/useAuthStore'

// const firstName = ref('')
// const lastName = ref('')
// const email = ref('')
// const password = ref('')
// const currentUser = ref('')
const error = ref(null)
const isRegistering = ref(false)
const authStore = useAuthStore()


const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'

// Valideringsschema
const schema = computed(() =>
  yup.object({
    email: yup.string().email('Ogiltig e-post').required('E-post krävs'),
    password: yup.string().required('Lösenord krävs'),
    ...(isRegistering.value && {
      firstName: yup.string().required('Förnamn krävs'),
      lastName: yup.string().required('Efternamn krävs'),
    }),
  })
)

const toggleMode = () => {
  isRegistering.value = !isRegistering.value
  error.value = null
}

const handleSubmit = async (values) => {
  console.log("Trycker på logga in/Skapa")


  if (isRegistering.value) {
    try {
      await axios.post(`${apiUrl}/api/users/create`, {
        firstName: values.firstName,
        lastName: values.lastName,
        email: values.email,
        password: values.password
      })
      await authStore.login(values)  // Här anropar du login i store
    } catch (err) {
      console.log(err)
    }
  } else {
    await authStore.login(values)   // Och här också
  }
}
// Form state
const { errors } = useForm()
</script>

<style scoped>
.card {
  max-width: 400px;
  margin: 2rem auto;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 0 10px #ccc;
}

.p-fluid{
  justify-content: center;
  align-items: center;
  text-align: center;
}

input {
  min-width: 234px;
}

.field{
  margin-bottom: 1rem;
  display: flex;
  justify-content: center;
}

.login-view-buttons{
  display: flex;
  justify-content: space-between;
  margin-left: 50px;
  margin-right: 50px;
}

button{
  font-size: 12px;
}

.p-error {
  font-size: 0.875rem;
  color: #f44336;
}

.p-invalid {
  border-color: #f44336;
}
</style>
