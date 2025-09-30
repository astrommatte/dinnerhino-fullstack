import { defineStore } from 'pinia'

export const useShoppingListStore = defineStore('shoppingList', {
  state: () => ({
    ingredients: []
  }),
  actions: {
    generateFromRecipes(recipes) {
      this.ingredients = []

      recipes.forEach(recipe => {
        recipe.ingredients.forEach(ingredient => {
          this.ingredients.push(ingredient)
        })
      })

      // valfritt: slå ihop dubbletter, summera mängder osv
    }
  }
})
