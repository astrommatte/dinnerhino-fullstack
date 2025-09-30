<template>
  <div
    class="swipe-card"
    @touchstart="startSwipe"
    @touchmove="moveSwipe"
    @touchend="endSwipe"
  >
    <RecipeCard :recipe="currentRecipe" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRecipeStore } from '../stores/useRecipeStore'
import RecipeCard from './RecipeCard.vue'

const recipeStore = useRecipeStore()
const currentRecipe = computed(() => recipeStore.recipes[recipeStore.currentIndex])

let startX = 0

const startSwipe = (e) => {
  startX = e.touches[0].clientX
}

const moveSwipe = (e) => {
  // valfritt: animation eller preview på swipe
}

const endSwipe = (e) => {
  const endX = e.changedTouches[0].clientX
  const diff = endX - startX

  if (diff > 50) {
    recipeStore.likeCurrentRecipe()
  } else if (diff < -50) {
    recipeStore.skipCurrentRecipe()
  }
}
</script>
