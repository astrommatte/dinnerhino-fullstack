<template>
  <div>
    <Panel header="Valda recept">
      <ul class="liked-recipes-list">
        <li v-for="recipe in recipeStore.likedRecipes" :key="recipe.id">
          {{ recipe.name }}
        </li>
      </ul>
      <div class="download-button">
        <Button icon="pi pi-download" @click="downloadAllDescriptionsPDF"/>
        <small style="display: block; margin-top: 0.5rem;">Laddar ner beskrivningarna som PDF</small>
      </div>
    </Panel>



    <Panel header="Ingredienser">
      <ul class="shopping-list">
        <li
          v-for="(item, index) in sortedShoppingList"
          :key="index"
          :class="{ completed: isMarkedAsAtHome(item.ingredientName) || completedItems.has(index) }"
          @click="toggleItem(index)"
        >
          {{ item.ingredientName }} – <em>{{ item.quantity }}</em>
        </li>
      </ul>
      <div class="delete-button">
        <Button v-tooltip="'Tar bort hela listan'" @click="deleteShoppingList">Ta bort hela listan</Button>
      </div>
    </Panel>
  </div>
</template>

<script setup>
import axios from 'axios';
import { ref, computed, onMounted, watch } from 'vue';
import { useShoppingListStore } from '@/stores/useShoppingListStore';
import { useRecipeStore } from '@/stores/useRecipeStore';
import { useToaster } from '@/stores/useToastStore';
import { hideLoading, showLoading } from '@/stores/useLoadingStore'
import { useConfirmationStore } from '@/stores/useConfirmationStore';
import jsPDF from 'jspdf';
import { Panel } from 'primevue'

const confirmationStore = useConfirmationStore()
const { showSuccessToast, showErrorToast } = useToaster()
const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const recipeStore = useRecipeStore()
const { isMarkedAsAtHome } = useShoppingListStore()
const shoppingListStore = useShoppingListStore()
const completedItems = ref(new Set())

const sortedShoppingList = computed(() => {
  return [...shoppingListStore.ingredients].sort((a, b) =>
    a.ingredientName.localeCompare(b.ingredientName, 'sv')
  )
})

function downloadAllDescriptionsPDF() {
  confirmationStore.confirm1(async () => {
    const recipes = recipeStore.likedRecipes
    if (!recipes.length) {
      alert('Inga recept att exportera!')
      return
    }
    const doc = new jsPDF()
    let yOffset = 20

    recipes.forEach((recipe, i) => {
      if (i > 0) yOffset += 10
      if (yOffset > 270) {
        doc.addPage()
        yOffset = 20
      }
      doc.setFontSize(16)
      doc.text(recipe.name || 'Unnamed recipe', 10, yOffset)
      yOffset += 10
      doc.setFontSize(12)
      const splitDescription = doc.splitTextToSize(recipe.description || '', 180)
      doc.text(splitDescription, 10, yOffset)
      yOffset += splitDescription.length * 7
    })

    doc.save('Alla_receptbeskrivningar.pdf')
  })
}

async function deleteShoppingList() {
  confirmationStore.confirm2(async () => {
    try {
      showLoading()
      await axios.delete(`${apiUrl}/api/shopping-list/clear`, {
        headers: { Authorization: localStorage.getItem('auth') }
      })
      showSuccessToast('Handlingslistan borttagen!')

    } catch (err) {
      showErrorToast('Gick ej att ta bort/nollställa handlingslistan!')
    } finally {
      shoppingListStore.setShoppingList([])
      shoppingListStore.setOverriddenItems([])

      localStorage.removeItem('overriddenItems')
      localStorage.removeItem('completedItems')
      
      recipeStore.reset()
      completedItems.value = new Set()
      hideLoading()
    }
  })
}

const toggleItem = (index) => {
  if (completedItems.value.has(index)) {
    completedItems.value.delete(index)
  } else {
    completedItems.value.add(index)
  }
  // Triggera reaktivitet på Set
  completedItems.value = new Set(completedItems.value)
}

onMounted(() => {
  const saved = localStorage.getItem('completedItems')
  if (saved) {
    try {
      const parsed = JSON.parse(saved)
      completedItems.value = new Set(parsed)
    } catch {
      completedItems.value = new Set()
    }
  }
})

// Spara till localStorage varje gång completedItems ändras
watch(
  completedItems,
  (newVal) => {
    // Spara som array (Set kan ej sparas direkt)
    localStorage.setItem('completedItems', JSON.stringify([...newVal]))
  },
  { deep: true }
)
</script>

<style scoped>
.download-button {
  text-align: center;
  margin-top: 1rem;
}

.liked-recipes-list,
.shopping-list {
  list-style: none;
  padding: 0;
  margin: 0.5rem 0 1rem 0;
}

.delete-button {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}

.shopping-list li {
  padding: 0.5rem;
  border-bottom: 1px solid var(--surface-border);
  cursor: pointer;
  user-select: none;
}

.shopping-list li:hover {
  background-color: var(--surface-hover);
}

.completed {
  text-decoration: line-through;
  color: var(--text-secondary-color);
  opacity: 0.7;
}
</style>
