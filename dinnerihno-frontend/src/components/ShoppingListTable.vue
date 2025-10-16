<template>
  <div>
    <Panel header="Valda recept">
      <ul class="liked-recipes-list">
        <li v-for="recipe in recipeStore.likedRecipes" :key="recipe.id">
          {{ recipe.name }}
          <Button 
            icon="pi pi-download" 
            @click="() => downloadRecipe(recipe)" 
            class="download-btn"
          />
        </li>
      </ul>
      <div class="download-all-btn-container">
      <Button 
        icon="pi pi-download" 
        label="Ladda ner alla"
        @click="downloadAllRecipes"
        class="download-all-btn"
      />
      </div>
      <small>*Ladda ner alla recept, eller enstaka.</small>

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
import { Panel } from 'primevue'

const confirmationStore = useConfirmationStore()
const { showSuccessToast, showErrorToast } = useToaster()
const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const recipeStore = useRecipeStore()
const { isMarkedAsAtHome } = useShoppingListStore()
const shoppingListStore = useShoppingListStore()
const completedItems = ref(new Set())

function downloadAllRecipes() {
  recipeStore.downloadAllRecipesPDF(recipeStore.likedRecipes)
}

function downloadRecipe(recipe) {
  recipeStore.downloadSinglePDF(recipe)
}

const sortedShoppingList = computed(() => {
  return [...shoppingListStore.ingredients].sort((a, b) =>
    a.ingredientName.localeCompare(b.ingredientName, 'sv')
  )
})

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
  .download-all-btn-container {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .download-all-btn{
    width: 10rem;
    height: 2rem;
  }

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
