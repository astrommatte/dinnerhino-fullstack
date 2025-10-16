<template>
  <div class="swipe-hint">
    <span class="arrow left">⬅</span>
    <span class="arrow right">➡</span>
  </div>
  <h4 class="title-text">Totalt antal recept: {{ totalAmountOfrecipes }}</h4>
  <div
    class="swipe-card"
    v-if="currentRecipe"
    @touchstart="startSwipe"
    @touchmove="moveSwipe"
    @touchend="endSwipe"
  >

    <div class="card">
      <h3 class="title-text">{{ currentRecipe.name }}</h3>
      <!-- SwipeCard.vue -->
      <div class="image-card" v-if="currentRecipe.image?.url">
        <img
          :src="currentRecipe.image.url"
          alt="Receptbild"
          class="recipe-thumbnail"
          @click="openImageDialog(currentRecipe.image.url)"
        />
      </div>

      <Dialog
        v-model:visible="imageDialogVisible"
        modal
        :style="{ width: '80vw' }"
        :closable="true"
        header="Receptbild"
      >
        <img
          :src="dialogImageUrl"
          alt="Stor receptbild"
          style="width: 100%; height: auto; object-fit: contain;"
        />
      </Dialog>



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

    <h4>Recept skapad av: {{ recipeOwnerUsername }}</h4>
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
import { Dialog } from 'primevue'

const { showSuccessToast, showInfoToast, showErrorToast } = useToaster()
const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const recipeStore = useRecipeStore()
const shoppingListStore = useShoppingListStore()
const currentRecipe = computed(() => recipeStore.recipes[recipeStore.currentIndex])
const totalAmountOfrecipes = computed(() => recipeStore.recipes.length)
const recipeOwnerUsername = computed(() => recipeStore.recipes[recipeStore.currentIndex].createdByUsername)
const infoModal = ref(false)
const imageDialogVisible = ref(false)
const dialogImageUrl = ref(null)

const openImageDialog = (url) => {
  dialogImageUrl.value = url
  imageDialogVisible.value = true
}
// Visa feedback till användaren när de gått igenom alla recept
// Reagera varje gång recepten startar om
watch(() => recipeStore.loopEvent, () => {
  showInfoToast('Finns inga fler recept, så nu börjar listan om, när du är klar tryck på "klar"')
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
    showSuccessToast('Hoppar över recept!')
    recipeStore.skipCurrentRecipe()
  } else if (diff < -50) {
    showSuccessToast('Recept inlagt i handlingslista!')
    recipeStore.likeCurrentRecipe()
  }
}

const moveSwipe = (e) => {
  // valfritt: preview swipe-riktning
}

// Starta om swipen från början
const reset = () => {
  showInfoToast('Börjar om och nollställer listan!')
  recipeStore.reset()
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
  background-color: var(--surface-card);
  color: var(--text-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.recipe-thumbnail {
  width: 100%;
  max-height: 200px;
  object-fit: cover;
  cursor: pointer;
  border-radius: 8px;
  transition: transform 0.2s ease;
}

.recipe-thumbnail:hover {
  transform: scale(1.03);
}

.image-card{
  display: flex;
  justify-content: center;
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
.swipe-hint {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
  padding: 0 1rem;
}

.arrow {
  font-size: 2rem;
  color: #999;
  animation: bounce 1.2s infinite;
}

.arrow.left {
  animation-direction: alternate-reverse;
}

@keyframes bounce {
  0% { transform: translateX(0); }
  50% { transform: translateX(8px); }
  100% { transform: translateX(0); }
}

@media (min-width: 900px) {
  /* Dölj pilarna på större skärmar om swipe-knappar används */
  .swipe-hint {
    display: none;
  }
}

@media (max-width: 900px) {
  .swipe-buttons {
    display: none;
  }
}
</style>
