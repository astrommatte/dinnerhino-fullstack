<template>
  <div>
    <h2 class="title-text">Mina Recept</h2>

    <!-- Knapp som visar / döljer formuläret -->
    <Button :label="showCreateForm ? 'Avbryt' : 'Skapa nytt recept' " @click="showCreateForm = !showCreateForm" />

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
    <OverlayPanel ref="op">
      <div v-if="selectedRecipe">
        <h3>{{ selectedRecipe.name }}</h3>
        <p>{{ selectedRecipe.description }}</p>
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
    </OverlayPanel>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import OverlayPanel from 'primevue/overlaypanel'

// Importera ditt formulär som komponent
import RecipeForm from '@/components/RecipeForm.vue'

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const recipes = ref([])
const selectedRecipe = ref(null)
const showCreateForm = ref(false)
const recipeToEdit = ref(null)

const fetchRecipes = async () => {
  const res = await axios.get(`${apiUrl}/api/recipes/my`, {
    headers: { Authorization: localStorage.getItem('auth') }
  })
  recipes.value = res.data
}

onMounted(fetchRecipes)

const showIngredients = (event, recipe) => {
  selectedRecipe.value = recipe
  // Access OverlayPanel via template ref
  op.value.toggle(event)
}

const op = ref(null)

// Ta bort recept
const deleteRecipe = async (id) => {
  if (!confirm('Är du säker på att du vill ta bort receptet?')) return

  try {
    await axios.delete(`${apiUrl}/api/recipes/${id}`, {
      headers: { Authorization: localStorage.getItem('auth') }
    })
    recipes.value = recipes.value.filter(r => r.id !== id)
    alert('Recept borttaget')
  } catch (err) {
    console.error(err)
    alert('Kunde inte ta bort receptet')
  }
}

// Editera recept - du kan navigera till en edit-sida eller visa ett formulär
const editRecipe = (recipe) => {
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
  color: #2a7ae2;
  text-decoration: underline;
  transition: color 0.3s;
}

.recipe-name:hover {
  color: #1d5bbf;
}
</style>

