<template>
  <div>
    <h2>Mina recept</h2>
    <div v-for="recipe in myRecipes" :key="recipe.id">
      <p>{{ recipe.name }}</p>
      <button @click="edit(recipe)">Redigera</button>
      <button @click="deleteRecipe(recipe.id)">Ta bort</button>
    </div>
    <button @click="createNew">Skapa nytt recept</button>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import api from '../axios'

const myRecipes = ref([])

onMounted(async () => {
  const res = await api.get('/api/recipes/mine') // förutsätter /mine-endpoint
  myRecipes.value = res.data
})

function deleteRecipe(id) {
  api.delete(`/api/recipes/${id}`).then(() => {
    myRecipes.value = myRecipes.value.filter(r => r.id !== id)
  })
}
</script>
