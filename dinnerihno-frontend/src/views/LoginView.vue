<template>
  <div class="card">
    <Form :validation-schema="schema" @submit="onSubmit">
      <div class="p-fluid">

        <!-- E-postfält -->
        <div class="field">
          <label for="email">E-post</label>
          <Field name="email" v-slot="{ field }">
            <InputText
              id="email"
              v-bind="field"
              :class="{ 'p-invalid': errors.email }"
            />
          </Field>
          <ErrorMessage name="email" class="p-error" />
        </div>

        <!-- Lösenord -->
        <div class="field">
          <label for="password">Lösenord</label>
          <Field name="password" v-slot="{ field }">
            <Password
              id="password"
              v-bind="field"
              toggleMask
              feedback="false"
              :class="{ 'p-invalid': errors.password }"
            />
          </Field>
          <ErrorMessage name="password" class="p-error" />
        </div>

        <!-- Knapp -->
        <Button type="submit" label="Logga in" class="mt-3" />
      </div>
    </Form>
  </div>
</template>

<script setup>
import { Form, Field, ErrorMessage, useForm } from 'vee-validate'
import * as yup from 'yup'

// PrimeVue-komponenter
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import Button from 'primevue/button'

// Valideringsschema
const schema = yup.object({
  email: yup.string().email('Ogiltig e-post').required('E-post krävs'),
  password: yup.string().min(6, 'Minst 6 tecken').required('Lösenord krävs'),
})

// Form state
const { errors } = useForm()

// Submit-funktion
const onSubmit = (values) => {
  console.log('Formulärdata:', values)
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

.field {
  margin-bottom: 1rem;
}

.p-error {
  font-size: 0.875rem;
  color: #f44336;
}

.p-invalid {
  border-color: #f44336;
}
</style>
