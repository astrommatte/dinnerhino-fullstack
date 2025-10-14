<template>
  <h2 class="title-text">Mina Recept</h2>
  <div class="card">
    
    <!-- Knapp som visar / döljer formuläret -->
    <Button
      class="cancel-create-buttons"
      :label="showCreateForm ? 'Avbryt' : 'Skapa nytt recept'" 
      @click="toggleForm" 
    />

    <!-- Formuläret visas när showCreateForm är true -->
    <RecipeForm
      v-if="showCreateForm"
      :existingRecipe="recipeToEdit"
      @saved="onRecipeSaved"
    />

    <ul class="recipe-list">
      <li v-for="recipe in recipes" :key="recipe.id">
        <span class="recipe-name" @click="showIngredients($event, recipe)">
          🍽️ {{ recipe.name }}
        </span>
      </li>
    </ul>


    <!-- OverlayPanel från PrimeVue -->
    <Popover ref="op">
      <div v-if="selectedRecipe">
        <h3>{{ selectedRecipe.name }}</h3>
        <p class="recipe-description">{{ selectedRecipe.description }}</p>
        <ul>
          <li v-for="(ing, i) in selectedRecipe.ingredients" :key="i">
            {{ ing.quantity }} {{ ing.unit }} {{ ing.name }}
          </li>
        </ul>

        <!-- Åtgärdsknappar -->
        <div class="actions">
          <Button label="Redigera" @click="editRecipe(selectedRecipe)" />
          <Button label="Ta bort" severity="danger" @click="deleteRecipe(selectedRecipe.id)" />
        </div>
      </div>
    </Popover>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import Button from 'primevue/button'

import { useConfirmationStore } from '@/stores/useConfirmationStore'
import RecipeForm from '@/components/RecipeForm.vue'
import { useToaster } from '@/stores/useToastStore'
import { showLoading, hideLoading } from '@/stores/useLoadingStore'

const { showSuccessToast, showInfoToast, showErrorToast } = useToaster()
const confirmationStore = useConfirmationStore()
const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const recipes = ref([])
const selectedRecipe = ref(null)
const showCreateForm = ref(false)
const recipeToEdit = ref(null)

const toggleForm = () => {
  if (showCreateForm.value) {
    recipeToEdit.value = null
  }
  showCreateForm.value = !showCreateForm.value
}

const fetchRecipes = async () => {
  showLoading()
  showInfoToast('Här finns dina personliga recept som du kan uppdatera/ta bort')
  try {
    const res = await axios.get(`${apiUrl}/api/recipes/my`, {
      headers: { Authorization: localStorage.getItem('auth') }
    })
  recipes.value = res.data
  }catch(err) {
    showErrorToast('Gick inte att hämta recept')
  } finally {
    showSuccessToast('Recept hämtade!')
    hideLoading()
  }

}

onMounted(()=> {
  fetchRecipes()
})

async function showIngredients(event, recipe) {
  selectedRecipe.value = recipe
  // Access OverlayPanel via template ref
  op.value.toggle(event)
}

const op = ref(null)

// Ta bort recept
async function deleteRecipe(id) {
  confirmationStore.confirm2(async () => {
    try {
      await axios.delete(`${apiUrl}/api/recipes/${id}`, {
        headers: { Authorization: localStorage.getItem('auth') }
      })
      recipes.value = recipes.value.filter(r => r.id !== id)
      showSuccessToast('Recept borttaget!')
    } catch (err) {
      showErrorToast('Gick inte att ta bort recept')
    } finally {
      op.value?.hide()
    }
  })
}

// Editera recept - du kan navigera till en edit-sida eller visa ett formulär
async function editRecipe(recipe) {
  op.value?.hide()
  selectedRecipe.value = null
  showCreateForm.value = true
  recipeToEdit.value = recipe
}

// Hantera event från CreateRecipeForm när nytt recept sparats
const onRecipeSaved = () => {
  showCreateForm.value = false
  recipeToEdit.value = null
  fetchRecipes()
}

</script>

<style scoped>
  .recipe-description {
    white-space: pre-wrap; /* 👈 gör så att radbrytningar och flera mellanslag bevaras */
    word-break: break-word; /* 👈 förhindrar overflow om ord är långa */
  }
  .cancel-create-buttons{
    width: 100%;
  }

  .recipe-list {
    list-style: none;
    padding: 0;
    margin: 1rem 0;
  }

  .recipe-list li {
    margin: 0.5rem 0;
  }

  .recipe-name {
    cursor: pointer;
    transition: color 0.3s;
  }

  .recipe-name:hover {
    color: #1d5bbf;
  }
</style>

