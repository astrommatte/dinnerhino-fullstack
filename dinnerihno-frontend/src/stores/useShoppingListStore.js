import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useShoppingListStore = defineStore('shoppingList', () => {
  const ingredients = ref([])

  // const generateFromRecipes = (recipes) => {
  //   ingredients.value = []
  //   recipes.forEach(recipe => {
  //     recipe.ingredients.forEach(ingredient => {
  //       ingredients.value.push(ingredient)
  //     })
  //   })
  // }

  const setShoppingList = (ingredientList) => {
    ingredients.value = ingredientList
  }

  // ✅ Här måste du returnera allt du vill kunna använda i komponenterna
  return {
    ingredients,
    //generateFromRecipes,
    setShoppingList
  }
})