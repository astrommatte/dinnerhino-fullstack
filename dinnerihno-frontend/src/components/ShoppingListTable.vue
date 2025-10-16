<template>
  <div>
    <Panel header="Valda recept">
      <ul class="liked-recipes-list">
        <li v-for="recipe in recipeStore.likedRecipes" :key="recipe.id">
          {{ recipe.name }}
          <Button 
            icon="pi pi-download" 
            @click="() => downloadSinglePDF(recipe)" 
            class="download-btn"
          />
        </li>
      </ul>
      <div class="download-button">
        <small>Ladda ner beskrivningarna som PDF</small>
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

const completedIngredientNames = computed(() => {
  return new Set(
    [...completedItems.value]
      .map(index => sortedShoppingList.value[index]?.ingredientName)
      .filter(Boolean)
  )
})

const sortedShoppingList = computed(() => {
  return [...shoppingListStore.ingredients].sort((a, b) =>
    a.ingredientName.localeCompare(b.ingredientName, 'sv')
  )
})

function downloadSinglePDF(recipe) {
  const doc = new jsPDF()
  let yOffset = 20
  const pageHeight = 280
  const marginX = 10
  const lineHeight = 7

  const shoppingListStore = useShoppingListStore()

  doc.setFontSize(16)
  doc.text(recipe.name || 'Unnamed recipe', marginX, yOffset)
  yOffset += lineHeight + 3

  doc.setFontSize(12)
  doc.text('Ingredienser:', marginX, yOffset)
  yOffset += lineHeight

  if (recipe.ingredients && recipe.ingredients.length > 0) {
    const filteredIngredients = recipe.ingredients.filter((ingredient) => {
      return !shoppingListStore.isMarkedAsAtHome(ingredient.name)
    })

    if (filteredIngredients.length === 0) {
      doc.text('Inga ingredienser att visa (alla är markerade)', marginX + 5, yOffset)
      yOffset += lineHeight
    } else {
      filteredIngredients.forEach((ingredient) => {
        const text = `• ${ingredient.name} – ${ingredient.quantity}`
        const splitText = doc.splitTextToSize(text, 180)
        doc.text(splitText, marginX + 5, yOffset)
        yOffset += lineHeight * splitText.length

        if (yOffset > pageHeight) {
          doc.addPage()
          yOffset = 20
        }
      })
    }
  } else {
    doc.text('Inga ingredienser angivna', marginX + 5, yOffset)
    yOffset += lineHeight
  }

  yOffset += 5

  doc.text('Beskrivning:', marginX, yOffset)
  yOffset += lineHeight

  const splitDescription = doc.splitTextToSize(recipe.description || 'Ingen beskrivning angiven', 180)
  doc.text(splitDescription, marginX, yOffset)

  const safeName = (recipe.name || 'Recept').replace(/[^a-z0-9_\-åäö ]/gi, '_')
  doc.save(`${safeName}.pdf`)
}

// function downloadSeparatePDFs() {
//   const recipes = recipeStore.likedRecipes
//   if (!recipes.length) {
//     alert('Inga recept att exportera!')
//     return
//   }

//   recipes.forEach((recipe) => {
//     const doc = new jsPDF()
//     let yOffset = 20
//     const pageHeight = 280
//     const marginX = 10
//     const lineHeight = 7

//     // Rubrik - receptnamn
//     doc.setFontSize(16)
//     doc.text(recipe.name || 'Unnamed recipe', marginX, yOffset)
//     yOffset += lineHeight + 3

//     // Ingredienser
//     doc.setFontSize(12)
//     doc.text('Ingredienser:', marginX, yOffset)
//     yOffset += lineHeight

//     if (recipe.ingredients && recipe.ingredients.length > 0) {
//       recipe.ingredients.forEach((ingredient) => {
//         const text = `• ${ingredient.name} – ${ingredient.quantity}`
//         const splitText = doc.splitTextToSize(text, 180)
//         doc.text(splitText, marginX + 5, yOffset)
//         yOffset += lineHeight * splitText.length
//         if (yOffset > pageHeight) {
//           doc.addPage()
//           yOffset = 20
//         }
//       })
//     } else {
//       doc.text('Inga ingredienser angivna', marginX + 5, yOffset)
//       yOffset += lineHeight
//     }

//     yOffset += 5

//     // Beskrivning
//     doc.text('Beskrivning:', marginX, yOffset)
//     yOffset += lineHeight

//     const splitDescription = doc.splitTextToSize(recipe.description || 'Ingen beskrivning angiven', 180)
//     doc.text(splitDescription, marginX, yOffset)
//     yOffset += lineHeight * splitDescription.length

//     // Spara filen med receptnamnet som filnamn (rensa filnamnet från ogiltiga tecken)
//     const safeName = (recipe.name || 'Recept').replace(/[^a-z0-9_\-åäö ]/gi, '_')
//     doc.save(`${safeName}.pdf`)
//   })
// }

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
.download-btn {
  width: 1.75rem;
  height: 1.75rem;
  margin: 4px;
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
