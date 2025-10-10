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
        <Button icon="pi pi-thumbs-up" @click="recipeStore.likeCurrentRecipe(), showSuccessToast('Recept inlagt i handlingslista!')" />
        <Button icon="pi pi-thumbs-down" @click="recipeStore.skipCurrentRecipe(), showSuccessToast('Hoppar över recept!')" />
      </div>
    </div>
    <Popover v-if="infoModal"/>

    <h4>Recept skapad av: {{ recipeOwnerEmail }}</h4>
  </div>
  
  <div class="reset-and-submit-buttons">
    <Button icon="pi pi-refresh" label="Avbryt" severity="warning" @click="reset" />
    <Button icon="pi pi-question-circle" @click="openInfoModal()"/>
    <Button icon="pi pi-check" label="Klar" severity="success" @click="submit" />

    <Dialog header="Instruktioner och info" v-model:visible="infoModal" modal closeOnEscape>
      <ul>
        <li>Swipea vänster för att lägga till.</li>
        <li>Swipea höger för att skippa.</li>
        <li>Om du redan har en ingrediens hemma, så kan du klicka bort den direkt innan du swipear, då kommer den vara överstruken direkt
        när du kommer till handlingslistan!</li>
        <li>Recepten kommer slumpmässigt.</li>
      </ul>
    </Dialog>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRecipeStore } from '../stores/useRecipeStore'
import { useShoppingListStore } from '@/stores/useShoppingListStore'
import { useToaster } from '@/stores/useToastStore'
import { hideLoading, showLoading } from '@/stores/useLoadingStore'
import router from '@/router'
import axios from 'axios'

const { showSuccessToast, showInfoToast, showErrorToast } = useToaster()
const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const recipeStore = useRecipeStore()
const shoppingListStore = useShoppingListStore()
const currentRecipe = computed(() => recipeStore.recipes[recipeStore.currentIndex])
const recipeOwnerEmail = computed(() => recipeStore.recipes[recipeStore.currentIndex].createdByEmail)
const infoModal = ref(false)

// Visa feedback till användaren när de gått igenom alla recept
// Reagera varje gång recepten startar om
watch(() => recipeStore.loopEvent, () => {
  // Visa feedback – du kan byta till toast, modal etc.
  alert('Du har gått igenom alla recept! De blandas nu om.')

  // Exempel med PrimeVue toast:
  // toast.add({
  //   severity: 'info',
  //   summary: 'Receptlistan börjar om',
  //   detail: 'Alla recept är visade. Nu visas en ny slumpad ordning.',
  //   life: 3000
  // })
})

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
    showSuccessToast('Hoppar över recept!')
  } else if (diff < -50) {
    recipeStore.likeCurrentRecipe()
    showSuccessToast('Recept inlagt i handlingslista!')
  }
}

const moveSwipe = (e) => {
  // valfritt: preview swipe-riktning
}

// Starta om swipen från början
const reset = () => {
  recipeStore.reset()
  showInfoToast('Börjar om och nollställer listan!')
}

// Skicka gillade recept till inköpslista (exempel)
const submit = async () => {
  const liked = recipeStore.likedRecipes
  if (liked.length === 0) {
    showInfoToast('Du har inga gillade recept att skicka till handlingslistan.')
    return
  }

  try {
    showLoading()
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
    showErrorToast('Kunde inte skicka recept till inköpslistan.')
  } finally {
    hideLoading()
  }
}

const openInfoModal = (() => {
  infoModal.value = true
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

@media (max-width: 900px) {
  .swipe-buttons {
    display: none;
  }
}
</style>
