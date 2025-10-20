<template>
  <div class="swipe-hint">
    <span class="arrow left">⬅</span>
    <Button icon="pi pi-question-circle" label="Hur gör man?" @click="openInfoModal()"/>
    <span class="arrow right">➡</span>
  </div>
  <h4 class="title-text">Totalt antal recept i databasen: {{ totalAmountOfrecipes }}</h4>
  <div
    class="swipe-card"
    v-if="currentRecipe"
    :style="{ transform: `translateX(${translateX}px)` }"
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

      <p><strong>Portioner:</strong> {{ currentRecipe.servings }}</p>

      <h4>Ingredienser:</h4>
      <ul>
        <li
          v-for="(ingredient, index) in currentRecipe.ingredients"
          :key="index"
          :style="{ textDecoration: ingredient.isAlreadyAtHome ? 'line-through' : 'none' }"
          @click="toggleAtHome(ingredient)"
        >
          {{ ingredient.name }} - {{ ingredient.quantity }}
        </li>
      </ul>
      <div class="swipe-buttons">
        <Button icon="pi pi-thumbs-down" @click="recipeStore.skipCurrentRecipe(), showSuccessToast('Hoppar över recept!')" />
        <Button icon="pi pi-thumbs-up" @click="recipeStore.likeCurrentRecipe(), showSuccessToast('Recept inlagt i handlingslista!')" />
      </div>
    </div>
    <Popover v-if="infoModal"/>

    <h4>Recept skapad av: {{ recipeOwnerUsername }}</h4>
  </div>
  
  <div class="reset-and-submit-buttons">
    <Button icon="pi pi-refresh" label="Avbryt" severity="warning" @click="reset" />
    <Button icon="pi pi-check" label="Klar" severity="success" @click="submit" />

    <Dialog header="Instruktioner och info" v-model:visible="infoModal" modal closeOnEscape>
      <ul>
        <li>Swipea höger för att lägga till.</li>
        <li>Swipea vänster för att skippa.</li>
        <li>Det funkar även att trycka på tumme upp/ner</li>
        <li>Om du redan har en ingrediens hemma, så kan du klicka bort den direkt innan du swipear, då kommer den vara överstruken direkt
        när du kommer till handlingslistan!</li>
        <li>I handlingslistan så kan du ladda hem receptets beskrivning samt ingredienser till en pdf-fil</li>
        <li>Ingredienserna som blir överstrukna i swipe-delen, kommer inte komma med i pdf-filen</li>
        <li>Recepten kommer slumpmässigt.</li>
        <li>Tryck klar när du känner dig redo att gå till nästa steg.</li>
        <li>Tryck på avbryt om du vill börja om.</li>
        <li>Om ni laddar hem receptet som pdf(i handlingslistan) så kommer beskrivningen med där!</li>
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
const translateX = ref(0)
const rotation = ref(0)
const isAnimating = ref(false)

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
  if (isAnimating.value) return
  startX = e.touches[0].clientX
}

const moveSwipe = (e) => {
  if (isAnimating.value) return
  const currentX = e.touches[0].clientX
  const diff = currentX - startX
  // ge lite rörelse, men dämpad
  translateX.value = diff * 0.6
}

const endSwipe = (e) => {
  const diff = e.changedTouches[0].clientX - startX

  if (diff > 100) {
    
    showSuccessToast('Recept inlagt i handlingslista!')
    recipeStore.likeCurrentRecipe()
  } else if (diff < -100) {
    showSuccessToast('Hoppar över recept!')
    recipeStore.skipCurrentRecipe()
    
  }

  resetPosition()
}

const resetPosition = () => {
  isAnimating.value = true
  translateX.value = 0
  rotation.value = 0
  setTimeout(() => (isAnimating.value = false), 200)
}

const reset = () => {
  showInfoToast('Börjar om och nollställer listan!')
  recipeStore.reset()
}

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
  width: 90%;
  max-width: 400px;
  margin: 2rem auto;
  padding: 1.5rem;
  background: var(--surface-card);
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.15);
  transition: transform 0.25s ease;
  touch-action: pan-y;
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
</style>
