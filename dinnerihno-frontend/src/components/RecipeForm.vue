<template>
  <div class="form-wrapper">
    <div class="card">
      <h2 class="title-text">Skapa nytt recept</h2>

      <div class="p-fluid">
        <!-- Receptinfo -->
        <div class="field">
          <label for="name"></label>
          <InputText v-model="recipe.name" id="name" placeholder="Namn" />
        </div>

        <div class="field textarea-wrapper">
          <FloatLabel variant="on">
            <label for="description"></label>
            <Textarea
              id="description"
              v-model="recipe.description"
              rows="5"
              maxlength="1000"
              autoResize
              placeholder="Beskrivning, max 1000 tecken."
            />
          </FloatLabel>

          <span class="char-counter-inside">
            {{ recipe.description.length }}/1000
          </span>
        </div>


        <div class="field">
          <label for="servings"></label>
          <InputNumber v-model="recipe.servings" id="servings" placeholder="Antal portioner" />
        </div>

        <!-- Ingredienser -->
        <h3 class="title-text">Ingredienser</h3>
        <div
          v-if="showIngredientForm"
          v-for="(ingredient, index) in recipe.ingredients"
          :key="index"
          class="p-inputgroup mb-2"
        >
          <InputText v-model="ingredient.name" placeholder="Namn" />
          <InputText v-model="ingredient.quantity" placeholder="Mängd" />
          <Button icon="pi pi-times-circle" severity="danger" @click="removeIngredient(index)" />
        </div>
        <Button label="Lägg till ingrediens" class="mt-2" @click="addIngredient" />

        <!-- Skapa-knapp -->
        <Button label="Spara recept" class="mt-4" @click="submitRecipe" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import InputNumber from 'primevue/inputnumber'
import Button from 'primevue/button'
import axios from 'axios'
import { useToaster } from '@/stores/useToastStore'
import { hideLoading, showLoading } from '@/stores/useLoadingStore'
import FloatLabel from 'primevue/floatlabel'

const { showSuccessToast, showErrorToast } = useToaster()
const showIngredientForm = ref(false)
// Du kan även använda computed om du vill visa "kvar" istället:
//const remainingChars = computed(() => 1000 - description.value.length)

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
    ingredients: []
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
  showIngredientForm.value = true
}

const removeIngredient = (index) => {
  recipe.value.ingredients.splice(index, 1)
}

const submitRecipe = async () => {
  try {
    showLoading()
    const headers = { Authorization: localStorage.getItem('auth') }

    if (props.existingRecipe && props.existingRecipe.id) {
      // Redigera recept
      await axios.put(`${apiUrl}/api/recipes/${props.existingRecipe.id}`, recipe.value, { headers })
      showSuccessToast('Recept uppdaterat!')
    } else {
      // Skapa nytt recept
      await axios.post(`${apiUrl}/api/recipes`, recipe.value, { headers })
      showSuccessToast('Recept skapat!')
    }

    emit('saved') // Meddela förälder

    resetForm()
  } catch (error) {
    showErrorToast('Något gick fel vid sparandet av recept.')
  } finally {
    hideLoading()
  }
}
</script>

<style scoped>
  .textarea-wrapper {
    position: relative;
  }

  .char-counter-inside {
    position: absolute;
    bottom: 8px;
    right: 10px;
    font-size: 12px;
    color: var(--text-secondary, #888);
    background: var(--surface-card);
    padding: 0 4px;
    border-radius: 4px;
    pointer-events: none; /* så att den inte stör skrivandet */
  }

  .form-wrapper {
    padding: 1rem;
    box-sizing: border-box;
  }

  .card {
    width: 100%;
  }

  .field label {
    font-weight: 600;
    margin-bottom: 0.5rem;
  }

  h2, h3 {
    margin-bottom: 1rem;
  }

  .p-inputgroup{
    margin-bottom: 1rem;
  }

  .p-inputgroup button{
    width: 15px;
    height: 10px;
    margin: 5px;
  }
</style>
