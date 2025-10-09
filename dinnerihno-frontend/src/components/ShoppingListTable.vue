<template>
  <div>
    <ul class="shopping-list">
      <h4>Antal recept valda i föregående sida: {{ recipeStore.likedRecipes.length }}</h4>
      <li
        v-for="(item, index) in sortedShoppingList"
        :key="index"
        :style="{ 
          textDecoration: isMarkedAsAtHome(item.ingredientName) || completedItems.has(index) ? 'line-through' : 'none',
          opacity: isMarkedAsAtHome(item.ingredientName) || completedItems.has(index) ? 0.2 : 1
        }"

        @click="toggleItem(index)"
      >
        {{ item.ingredientName }} – <em>{{ item.quantity }}</em>
      </li>
    </ul>
    <div class="delete-button">
      <Button @click="deleteShoppingList">Ta bort hela listan</Button>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { ref, computed, onMounted, watch } from 'vue';
import { useShoppingListStore } from '@/stores/useShoppingListStore';
import { useRecipeStore } from '@/stores/useRecipeStore';
import { useToaster } from '@/stores/useToastStore';
import { hideLoading, showLoading } from '@/stores/useLoadingStore'

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

const deleteShoppingList = async () => {
  if (!confirm('Är du säker på att du vill ta bort hela handlingslistan?')) return

  try {
    showLoading()
    await axios.delete(`${apiUrl}/api/shopping-list/clear`, {
      headers: { Authorization: localStorage.getItem('auth') }
    })
    showSuccessToast('Handlingslistan borttagen')

  } catch (err) {
    console.error(err)
    showErrorToast('Gick ej att ta bort/nollställa handlingslistan!')
  } finally {
    shoppingListStore.setShoppingList([])
    recipeStore.reset()
    completedItems.value = new Set()
    localStorage.removeItem('completedItems')
    hideLoading()
  }
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
.shopping-list {
  text-align: center;
  list-style: none;
  padding: 0;
  margin: 1rem 0;
}

.delete-button {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}

.shopping-list li {
  padding: 0.5rem;
  border-bottom: 1px solid #e0e0e0;
  cursor: pointer;
  user-select: none;

}

.shopping-list li:hover {
  background: #f5f5f5;
}

.completed {
  text-decoration: line-through;
  color: gray;
  opacity: 0.7;
}
</style>
