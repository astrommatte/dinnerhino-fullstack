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

      <h4>Ingredienser:</h4>
      <ul>
        <li
          v-for="(ingredient, index) in currentRecipe.ingredients"
          :key="index"
          :style="{ textDecoration: ingredient.isAlreadyAtHome ? 'line-through' : 'none' }"
          @click="toggleAtHome(ingredient)"
        >
          {{ ingredient.quantity }} {{ ingredient.unit }} {{ ingredient.name }}
        </li>
      </ul>
      <div class="swipe-buttons">
        <Button icon="pi pi-thumbs-up" @click="recipeStore.likeCurrentRecipe()" />
        <Button icon="pi pi-thumbs-down" @click="recipeStore.skipCurrentRecipe()" />
      </div>
    </div>
    <Popover v-if="infoModal"/>

    <h4>Recept skapad av: {{ recipeOwnerEmail }}</h4>
  </div>
  
  <div class="reset-and-submit-buttons">
    <Button icon="pi pi-refresh" label="Avbryt" severity="warning" @click="reset" />
    <Button icon="pi pi-question-circle" @click="openInfoModal()"/>
    <Button icon="pi pi-check" label="Klar" severity="success" @click="submit" />

    <Dialog header="Instruktioner" v-model:visible="infoModal" modal closeOnEscape>
      <ul>
        <li>Swipea vänster för att lägga till</li>
        <li>Swipea höger för att skippa</li>
        <li>Klicka på en ingrediens om du redan har den hemma</li>
      </ul>
    </Dialog>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRecipeStore } from '../stores/useRecipeStore'
import { useShoppingListStore } from '@/stores/useShoppingListStore'
import router from '@/router'
import axios from 'axios'

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const recipeStore = useRecipeStore()
const shoppingListStore = useShoppingListStore()
const currentRecipe = computed(() => recipeStore.recipes[recipeStore.currentIndex])
const recipeOwnerEmail = computed(() => recipeStore.recipes[recipeStore.currentIndex].createdByEmail)
const infoModal = ref(false)

const toggleAtHome = (ingredient) => {
  ingredient.isAlreadyAtHome = !ingredient.isAlreadyAtHome
}

let startX = 0

const startSwipe = (e) => {
  startX = e.touches[0].clientX
}

const endSwipe = (e) => {
  const endX = e.changedTouches[0].clientX
  const diff = endX - startX

  if (diff > 50) {
    recipeStore.skipCurrentRecipe()
  } else if (diff < -50) {
    recipeStore.likeCurrentRecipe()
  }
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
    await Promise.all(
      liked.map(recipe =>
        axios.post(`${apiUrl}/api/shopping-list/add/${recipe.id}`, null, {
          headers: {
            Authorization: localStorage.getItem('auth'),
          }
        })
      )
    )

    // 💾 Spara de lokala ingrediens-flaggorna
    shoppingListStore.setOverriddenItems(
      liked.flatMap(recipe =>
        recipe.ingredients.map(ingredient => ({
          name: ingredient.name,
          isAlreadyAtHome: !!ingredient.isAlreadyAtHome
        }))
      )
    )

    router.push('/shoppinglist')
  } catch (error) {
    console.error("Fel vid skick av recept:", error)
    alert('Kunde inte skicka recept till inköpslistan.')
  }
}

const openInfoModal = (() => {
  console.log('Nu ska det öppnas en modal med INFO')
  infoModal.value = true
})

const closeInfoModal = (() => {
  console.log('Stänger info modalen')
  infoModal.value = false
})
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
  display: flex;
  max-width: 500px;
  justify-content: space-between;
  margin: auto;
}

.swipe-buttons{
  display: flex;
  margin: auto;
  justify-content: space-between;
}

@media (max-width: 1024px) {
  .swipe-buttons {
    display: none;
  }
}
</style>
