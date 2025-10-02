<template>
  <div
    class="swipe-card"
    v-if="currentRecipe"
    @touchstart="startSwipe"
    @touchmove="moveSwipe"
    @touchend="endSwipe"
  >
    <div class="card">
      <h3>{{ currentRecipe.name }}</h3>
      <p>{{ currentRecipe.description }}</p>
      <p><strong>Portioner:</strong> {{ currentRecipe.servings }}</p>

      <h4>Ingredienser</h4>
      <ul>
        <li
          v-for="(ingredient, index) in currentRecipe.ingredients"
          :key="index"
        >
          {{ ingredient.quantity }} {{ ingredient.unit }} {{ ingredient.name }}
        </li>
      </ul>
      <Button icon="pi pi-thumbs-up" @click="recipeStore.likeCurrentRecipe()" />
      <Button icon="pi pi-times" @click="recipeStore.skipCurrentRecipe()" />
    </div>
  </div>
  

  <div class="reset-and-submit-buttons">
    <Button icon="pi pi-refresh" label="Avbryt" severity="warning" @click="reset" />
    <Button icon="pi pi-check" label="Klar" severity="success" @click="submit" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRecipeStore } from '../stores/useRecipeStore'
import router from '@/router'
import axios from 'axios'

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const recipeStore = useRecipeStore()
const currentRecipe = computed(() => recipeStore.recipes[recipeStore.currentIndex])

let startX = 0

const startSwipe = (e) => {
  startX = e.touches[0].clientX
}

const moveSwipe = (e) => {
  // valfritt: preview swipe-riktning
}

// Starta om swipen från början
const reset = () => {
  recipeStore.reset()
}

// Skicka gillade recept till inköpslista (exempel)
const submit = async () => {
  const liked = recipeStore.likedRecipes
  if (liked.length === 0) {
    alert('Du har inga gillade recept att skicka.')
    return
  }

  try {
    // 🔽 Här skickar vi alla gillade recept parallellt till backend
    await Promise.all(
      liked.map(recipe =>
        axios.post(`${apiUrl}/api/shopping-list/add/${recipe.id}`, null, {
          headers: {
            Authorization: localStorage.getItem('auth'),
          }
        })
      )
    )

    // ✅ Navigera till shoppinglistan när alla POST-anrop är klara
    router.push('/shoppinglist')
  } catch (error) {
    console.error("Fel vid skick av recept:", error)
    alert('Kunde inte skicka recept till inköpslistan.')
  }
}


const endSwipe = (e) => {
  const endX = e.changedTouches[0].clientX
  const diff = endX - startX

  if (diff > 50) {
    recipeStore.likeCurrentRecipe()
  } else if (diff < -50) {
    recipeStore.skipCurrentRecipe()
  }
}
</script>

<style scoped>
.swipe-card {
  max-width: 500px;
  margin: auto;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card h3 {
  margin-top: 0;
}

.card ul {
  padding-left: 1.2rem;
}

.reset-and-submit-buttons{
  max-width: 500px;
  margin: auto;
}
</style>
