// stores/useRecipeStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

const likedRecipes = ref([])

export const useRecipeStore = defineStore('recipe', () => {
  const recipes = ref([])
  const currentIndex = ref(0)

  const setRecipes = (newRecipes) => {
    recipes.value = newRecipes
    currentIndex.value = 0
  }

  const likeCurrentRecipe = () => {
    console.log('Gillade recept:', recipes.value[currentIndex.value])
    likedRecipes.value.push(recipes.value[currentIndex.value])
    nextRecipe()
  }

  const skipCurrentRecipe = () => {
    console.log('Skippade recept:', recipes.value[currentIndex.value])
    nextRecipe()
  }

  const nextRecipe = () => {
    if (currentIndex.value < recipes.value.length - 1) {
      currentIndex.value++
    } else {
      console.log('Inga fler recept.')
    }
  }

  function reset() {
    currentIndex.value = 0
    likedRecipes.value = []
  }

  return {
    recipes,
    currentIndex,
    likedRecipes,
    setRecipes,
    likeCurrentRecipe,
    skipCurrentRecipe,
    reset,
  }
})
