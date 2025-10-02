<template>
  <div>
    <h2 class="title-text">Adminpanel</h2>
    <div v-for="user in users" :key="user.id">
      <p>{{ user.username }} - {{ user.roles.join(', ') }}</p>
      <button @click="deleteUser(user.id)">Ta bort användare</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../axios'

const users = ref([])

onMounted(async () => {
  const res = await api.get('/api/users') // kräver admin
  users.value = res.data
})

function deleteUser(id) {
  api.delete(`/api/users/${id}`).then(() => {
    users.value = users.value.filter(u => u.id !== id)
  })
}
</script>
