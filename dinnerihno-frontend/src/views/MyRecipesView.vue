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
      :allRecipes="allRecipes"
      @saved="onRecipeSaved"
      @imageDeleted="onImageDeleted"
    />

    <ul class="recipe-list">
      <li v-for="recipe in recipes" :key="recipe.id">
        <span class="recipe-name" @click="showIngredients($event, recipe)">
          🍽️ {{ recipe.name }}
        </span>
      </li>
    </ul>


    <Popover ref="op" dismissable>
      <div v-if="selectedRecipe" class="popover-content">
        <h3>{{ selectedRecipe.name }}</h3>
        <p class="recipe-description">{{ selectedRecipe.description }}</p>

        <!-- Visa bilden -->
        <div v-if="selectedRecipe.image" class="recipe-image-container" style="position: relative; display: inline-block;">
          <img
            :src="selectedRecipe.image.url"
            alt="Receptbild"
            class="popover-image"
            @click="showLargeImage = true"
          />
        </div>
        <!-- Ingredienser -->
        <ul>
          <li v-for="(ing, i) in selectedRecipe.ingredients" :key="i">
            {{ ing.name }} - {{ ing.quantity }}
          </li>
        </ul>

        <!-- Åtgärder -->
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
const allRecipes = ref([])
const selectedRecipe = ref(null)
const showCreateForm = ref(false)
const recipeToEdit = ref(null)

const toggleForm = () => {
  if (showCreateForm.value) {
    recipeToEdit.value = null
  }
  showCreateForm.value = !showCreateForm.value
}

const fetchMyRecipes = async () => {
  showLoading()
  try {
    const res = await axios.get(`${apiUrl}/api/recipes/my`, {
      headers: { Authorization: localStorage.getItem('auth') }
    })
  recipes.value = res.data
  }catch(err) {
    showErrorToast('Gick inte att hämta recept')
  } finally {
    hideLoading()
  }
}

const fetchAllRecipes = async () => {
  try {
    const res = await axios.get(`${apiUrl}/api/recipes`, {
      headers: { Authorization: localStorage.getItem('auth') }
    })
  allRecipes.value = res.data
  }catch(err) {

  } finally {

  }
}

onMounted(()=> {
  fetchMyRecipes()
  fetchAllRecipes()
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
      op.value?.hide()
      showLoading()
      await axios.delete(`${apiUrl}/api/recipes/${id}`, {
        headers: { Authorization: localStorage.getItem('auth') }
      })
      recipes.value = recipes.value.filter(r => r.id !== id)
      showSuccessToast('Recept borttaget!')
    } catch (err) {
      showErrorToast('Gick inte att ta bort recept')
    } finally {
      hideLoading()
      op.value?.hide()
    }
  })
}

// Editera recept - du kan navigera till en edit-sida eller visa ett formulär
async function editRecipe(recipe) {
  op.value?.hide()
  selectedRecipe.value = { ...recipe }
  showCreateForm.value = true
  recipeToEdit.value = recipe
}

// Hantera event från CreateRecipeForm när nytt recept sparats
const onRecipeSaved = () => {
  showCreateForm.value = false
  recipeToEdit.value = null
  fetchMyRecipes()
}

const onImageDeleted = (recipeId) => {
  // Uppdatera det som visas i formuläret
  if (recipeToEdit.value?.id === recipeId) {
    recipeToEdit.value = {
      ...recipeToEdit.value,
      image: null // 👈 DETTA gör att v-if slutar gälla
    };
  }

  // Uppdatera det som visas i listan (om du har bild där med)
  const index = recipes.value.findIndex(r => r.id === recipeId);
  if (index !== -1) {
    recipes.value.splice(index, 1, {
      ...recipes.value[index],
      image: null // 👈 detta också
    });
  }

  // Uppdatera Popovern om den är öppen
  if (selectedRecipe.value?.id === recipeId) {
    selectedRecipe.value = {
      ...selectedRecipe.value,
      image: null
    };
  }
}

</script>

<style scoped>

  .popover-content {
    max-width: 300px; /* Justera efter behov */
    max-height: 400px;
    overflow-y: auto;
    padding: 1rem;
  }

  .recipe-description {
    white-space: pre-wrap; /* 👈 gör så att radbrytningar och flera mellanslag bevaras */
    word-break: break-word; /* 👈 förhindrar overflow om ord är långa */
  }
  .cancel-create-buttons{
    width: 100%;
  }

  .popover-image {
    max-width: 150px;
    max-height: 100px;
    border-radius: 4px;
    object-fit: cover;
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

