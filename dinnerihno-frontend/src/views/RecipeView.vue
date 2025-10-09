<template>
  <div>
    <h2 class="title-text">Swipea Recept</h2>

    <SwipeCard v-if="hasRecipes" />
    <div v-else>
      <p>Inga recept att visa.</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import axios from 'axios'
import SwipeCard from '../components/SwipeCard.vue'
import { useRecipeStore } from '../stores/useRecipeStore'
import { useToaster } from '@/stores/useToastStore'

const { showErrorToast } = useToaster()
const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const recipeStore = useRecipeStore()

const hasRecipes = computed(() => recipeStore.recipes.length > 0)

const fetchRecipes = async () => {
  try {
    const res = await axios.get(`${apiUrl}/api/recipes`, {
      headers: { Authorization: localStorage.getItem('auth') }
    })
    recipeStore.setRecipes(res.data)
  } catch (err) {
    showErrorToast('Kunde inte hämta recept')
  }
}

onMounted(() => {
  fetchRecipes()
})
</script>
