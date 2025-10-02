<template>
  <div>
    <h1>Handlingslista:</h1>
    <ul class="shopping-list">
      <li
        v-for="(item, index) in shoopingListItems"
        :key="index"
        :class="{ completed: completedItems.has(index) }"
        @click="toggleItem(index)"
      >
        {{ item.ingredientName }} –
        <em>{{ item.quantity }}</em>
      </li>
    </ul>
    <Button @click="deleteShoppingList">Ta bort hela listan</Button>
  </div>


    <div>
    <h1>Receptnamn:</h1>
    <ul>
      <li
        v-for="(item, index) in shoopingListItems"
        :key="index"
        @click="deleteItem(index)"
      >
        <strong>{{ item.recipeName }}</strong>:
      </li>
    </ul>
  </div>
</template>

<script setup>
import axios from 'axios';
import { ref, onMounted } from 'vue';

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const shoopingListItems = ref([])
const completedItems = ref(new Set())

const fetchShoppinglist = async () => {
  try {
    const res = await axios.get(`${apiUrl}/api/shopping-list`, {
      headers: { Authorization: localStorage.getItem('auth') }
    })
    shoopingListItems.value = res.data
  } catch (err) {
    console.error('Kunde inte hämta handlingslista:', err)
  }
}

const deleteShoppingList = async () => {
  if (!confirm('Är du säker på att du vill ta bort hela handlingslistan?')) return

  try {
    await axios.delete(`${apiUrl}/api/shopping-list/clear`, {
      headers: { Authorization: localStorage.getItem('auth') }
    })
    alert('Handlingslista borttagen')
  } catch (err) {
    console.error(err)
    alert('Kunde inte ta bort handlingslistan')
  }
}

// const deleteItem = async (id) => {
//   if (!confirm('Är du säker på att du vill ta bort receptet?')) return

//   try {
//     await axios.delete(`${apiUrl}/api/shopping-list/${id}`, {
//       headers: { Authorization: localStorage.getItem('auth') }
//     })
//     recipes.value = recipes.value.filter(r => r.id !== id)
//     alert('Recept borttaget')
//   } catch (err) {
//     console.error(err)
//     alert('Kunde inte ta bort receptet')
//   }
// }

onMounted(fetchShoppinglist)

const toggleItem = (index) => {
  if (completedItems.value.has(index)) {
    completedItems.value.delete(index)
  } else {
    completedItems.value.add(index)
  }
}
</script>

<style scoped>
.shopping-list {
  list-style: none;
  padding: 0;
  margin: 1rem 0;
}

.shopping-list li {
  padding: 0.5rem;
  border-bottom: 1px solid #e0e0e0;
  cursor: pointer;
  user-select: none;

}

.shopping-list li:hover {
  background: #f5f5f5;
}

.completed {
  text-decoration: line-through;
  color: gray;
  opacity: 0.7;
}
</style>
