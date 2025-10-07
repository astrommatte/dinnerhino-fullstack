<template>
  <div>
    <h1>Användarlista:</h1>
    <ul class="user-list">
      <li
        v-for="(user, index) in sortedUserList"
        :key="index"
      >
        {{ user.firstName }} – <em>{{ user.lastName }}</em>
        <Button icon="pi pi-pencil" severity="info" @click="updateUser(user.id)"/>
        <Button icon="pi pi-trash" severity="danger" @click="deleteUser(user.id)"/>
      </li>
    </ul>

  </div>
</template>

<script setup>
import axios from 'axios';
import { ref, onMounted, computed } from 'vue';
import { useUserListStore } from '@/stores/useUserStore';

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const userStore = useUserListStore()
const users = ref([])

const sortedUserList = computed(() => {
    return [...userStore.users].sort((a, b) =>
    a.lastName.localeCompare(b.lastName, 'sv')
  )
})

const updateUser = async (id) => {
  console.log("Klickar på uppdatera användare", id)
}

const deleteUser = async (id) => {
  console.log("Klickar på ta bort användare", id)
    try {
    await axios.delete(`${apiUrl}/api/users/${id}`, {
      headers: { Authorization: localStorage.getItem('auth') }
    })
    userStore.users.value = users.value.filter(u => u.id !== id)
    alert('Användare borttagen')
  } catch (err) {
    console.error(err)
    alert('Kunde inte ta bort användaren')
  } finally{
    fetchUsers()
  }
}

const fetchUsers = async () => {
  try {
    const res = await axios.get(`${apiUrl}/api/users`, {
      headers: { Authorization: localStorage.getItem('auth') }
    })
    userStore.setUserList(res.data)
    console.log(res.data)
  } catch (err) {
    console.error('Kunde inte hämta användare:', err)
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
