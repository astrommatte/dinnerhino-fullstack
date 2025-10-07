import { defineStore } from 'pinia'
import { ref } from 'vue'

const overriddenItems = ref([])

export const useShoppingListStore = defineStore('shoppingList', () => {
  const ingredients = ref([])

  const setShoppingList = (ingredientList) => {
    ingredients.value = ingredientList
  }

  const setOverriddenItems = (items) => {
    overriddenItems.value = items
  }

  const isMarkedAsAtHome = (ingredientName) => {
    return overriddenItems.value.some(item => item.name === ingredientName && item.isAlreadyAtHome)
  }

  // ✅ Här måste du returnera allt du vill kunna använda i komponenterna
  return {
    ingredients,
    setShoppingList,
    overriddenItems,
    setOverriddenItems,
    isMarkedAsAtHome,
  }
})