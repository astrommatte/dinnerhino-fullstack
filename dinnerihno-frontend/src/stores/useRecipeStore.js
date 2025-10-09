// stores/useRecipeStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

const likedRecipes = ref([])

export const useRecipeStore = defineStore('recipe', () => {
  const recipes = ref([])
  const currentIndex = ref(0)

  const setRecipes = (newRecipes) => {
    recipes.value = shuffle(newRecipes)
    currentIndex.value = 0
  }


  const likeCurrentRecipe = () => {
    likedRecipes.value.push(recipes.value[currentIndex.value])
    nextRecipe()
  }

  const skipCurrentRecipe = () => {
    nextRecipe()
  }

  const nextRecipe = () => {
    if (currentIndex.value < recipes.value.length - 1) {
      currentIndex.value++
    } else {
      startOver()
    }
  }

  function reset() {
    currentIndex.value = 0
    likedRecipes.value = []
    recipes.value = shuffle(recipes.value)
    currentIndex.value = 0
  }

  function startOver() {
    // Skapa en ny slumpad ordning av recepten
    recipes.value = shuffle(recipes.value)
    currentIndex.value = 0
  }

  function shuffle(array) {
    return [...array].sort(() => Math.random() - 0.5)
  }


  return {
    recipes,
    currentIndex,
    likedRecipes,
    setRecipes,
    likeCurrentRecipe,
    skipCurrentRecipe,
    reset,
    startOver,
  }
})
