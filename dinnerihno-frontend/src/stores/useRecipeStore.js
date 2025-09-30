import { defineStore } from 'pinia'

export const useRecipeStore = defineStore('recipes', {
  state: () => ({
    recipes: [],           // alla hämtade recept
    currentIndex: 0,       // för swiping
    selectedRecipes: [],   // de användaren gillar
  }),
  actions: {
    async fetchRecipes() {
      const res = await fetch('/api/recipes')
      this.recipes = await res.json()
    },
    likeCurrentRecipe() {
      const current = this.recipes[this.currentIndex]
      this.selectedRecipes.push(current)
      this.currentIndex++
    },
    skipCurrentRecipe() {
      this.currentIndex++
    }
  }
})
