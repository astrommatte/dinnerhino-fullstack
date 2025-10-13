<template>
    <Dialog
    :header="props.title"
    :visible="props.modelValue"
    modal
    :style="{ width: '400px' }"
    @update:visible="(val) => emit('update:modelValue', val)"
    >
    
    <div class="p-fluid">
      <InputText id="firstName" v-model="user.firstName" placeholder="Förnamn" />

      <InputText id="lastName" v-model="user.lastName" placeholder="Efternamn" />

      <InputText id="username" v-model="user.username" placeholder="Användarnamn" />

      <Password id="password" v-model="user.password" toggleMask placeholder="Lösenord" />

      <div class="field-checkbox">
        <ToggleSwitch inputId="isActive" v-model="user.active" :binary="true" />
        <label for="isActive">Aktiv</label>
      </div>
    </div>

    <template #footer>
      <Button label="Avbryt" icon="pi pi-times" class="p-button-text" @click="cancel" />
      <Button label="Spara" icon="pi pi-check" @click="save" />
    </template>
  </Dialog>
</template>

<script setup>
import { reactive, watch } from 'vue'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import { ToggleSwitch } from 'primevue'
import Button from 'primevue/button'

const props = defineProps({
  modelValue: Boolean,
  userData: Object,
  title: {
    type: String,
    default: 'Redigera användare'
  }
})

const emit = defineEmits(['update:modelValue', 'save', 'cancel'])

const user = reactive({
  id: null,
  firstName: '',
  lastName: '',
  username: '',
  password: '',
  isActive: false
})

// Uppdatera reaktiv user när props.userData ändras
watch(() => props.userData, (newVal) => {
  if (newVal) {
    Object.assign(user, newVal)
    user.password = '' // reset password vid edit
  }
}, { immediate: true })

const visible = reactive({
  value: props.modelValue
})

// Watch modelValue för att uppdatera synlighet
watch(() => props.modelValue, (val) => {
  visible.value = val
})

function cancel() {
  emit('update:modelValue', false)
  emit('cancel')
}

function save() {
  emit('save', { ...user })
  emit('update:modelValue', false)
}
</script>

<style scoped>
/* Lägg till valfri styling */
</style>
