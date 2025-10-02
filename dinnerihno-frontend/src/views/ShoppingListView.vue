<template>
  <div>
    <h2 class="title-text">Handlingslista</h2>

    <ShoppingListTable v-if="hasShoppingList" />
    <div v-else>
      <p>Ingen handlingslista att visa.</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import axios from 'axios'
import ShoppingListTable from '@/components/ShoppingListTable.vue'
import { useShoppingListStore } from '@/stores/useShoppingListStore'

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const shoppingListStore = useShoppingListStore()

const hasShoppingList = computed(() => shoppingListStore.ingredients.length > 0)

const fetchShoppingList = async () => {
  try {
    const res = await axios.get(`${apiUrl}/api/shopping-list`, {
      headers: { Authorization: localStorage.getItem('auth') }
    })
    shoppingListStore.setShoppingList(res.data)
    console.log(res.data)
  } catch (err) {
    console.error('Kunde inte hämta recept:', err)
  }
}

onMounted(fetchShoppingList)
</script>
