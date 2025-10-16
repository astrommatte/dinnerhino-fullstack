import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useShoppingListStore = defineStore('shoppingList', () => {
  const ingredients = ref([])

  // Läs overriddenItems från localStorage vid initiering
  const overriddenItems = ref(
    JSON.parse(localStorage.getItem('overriddenItems') || '[]')
  )

  const setShoppingList = (ingredientList) => {
    ingredients.value = ingredientList
  }

  const setOverriddenItems = (items) => {
    overriddenItems.value = items
  }

const isMarkedAsAtHome = (ingredientName) => {
  const nameLower = ingredientName.toLowerCase()
  return overriddenItems.value.some(
    item => item.name.toLowerCase() === nameLower && item.isAlreadyAtHome
  )
}

  // Spara overriddenItems i localStorage varje gång den ändras
  watch(overriddenItems, (newVal) => {
    localStorage.setItem('overriddenItems', JSON.stringify(newVal))
  }, { deep: true })

  return {
    ingredients,
    setShoppingList,
    overriddenItems,
    setOverriddenItems,
    isMarkedAsAtHome,
  }
})
