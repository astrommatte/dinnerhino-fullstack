<template>
  <div class="card">
    <h2>Skapa nytt recept</h2>

    <div class="p-fluid">
      <!-- Receptinfo -->
      <div class="field">
        <label for="name">Namn</label>
        <InputText v-model="recipe.name" id="name" />
      </div>

      <div class="field">
        <label for="description">Beskrivning</label>
        <Textarea v-model="recipe.description" id="description" rows="3" />
      </div>

      <div class="field">
        <label for="servings">Portioner</label>
        <InputNumber v-model="recipe.servings" id="servings" />
      </div>

      <!-- Ingredienser -->
      <h3>Ingredienser</h3>
      <div v-for="(ingredient, index) in recipe.ingredients" :key="index" class="p-inputgroup">
        <InputText v-model="ingredient.name" placeholder="Namn" />
        <InputText v-model="ingredient.quantity" placeholder="Mängd" />
        <Button icon="pi pi-times" severity="danger" @click="removeIngredient(index)" />
      </div>

      <Button label="Lägg till ingrediens" class="mt-2" @click="addIngredient" />

      <!-- Skapa-knapp -->
      <Button label="Spara recept" class="mt-4" @click="submitRecipe" />
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import InputNumber from 'primevue/inputnumber'
import Button from 'primevue/button'
import axios from 'axios'

const props = defineProps({
  existingRecipe: {
    type: Object,
    default: null
  }
})

const resetForm = () => {
  recipe.value = {
    name: '',
    description: '',
    servings: 1,
    ingredients: [{ name: '', quantity: '' }]
  }
}

const emit = defineEmits(['saved'])

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const recipe = ref({
  name: '',
  description: '',
  servings: 1,
  ingredients: [
    { name: '', quantity: '' }  // quantity som string, utan unit
  ]
})

watch(() => props.existingRecipe, (newVal) => {
  if (newVal) {
    recipe.value = JSON.parse(JSON.stringify(newVal)) // Deep copy
  } else {
    resetForm()
  }
}, { immediate: true })
const addIngredient = () => {
  recipe.value.ingredients.push({ name: '', quantity: '' })
}

const removeIngredient = (index) => {
  recipe.value.ingredients.splice(index, 1)
}

const submitRecipe = async () => {
  try {
    const headers = { Authorization: localStorage.getItem('auth') }

    if (props.existingRecipe && props.existingRecipe.id) {
      // Redigera recept
      await axios.put(`${apiUrl}/api/recipes/${props.existingRecipe.id}`, recipe.value, { headers })
      alert('Recept uppdaterat!')
    } else {
      // Skapa nytt recept
      await axios.post(`${apiUrl}/api/recipes`, recipe.value, { headers })
      alert('Recept skapat!')
    }

    emit('saved') // Meddela förälder

    resetForm()
  } catch (error) {
    console.error(error)
    alert('Något gick fel vid sparandet av recept.')
  }
}
</script>
