<template>
  <div>
    <h2 class="title-text">Admin</h2>

    <UserListTable v-if="hasUsers" />
    <div v-else>
      <p>Inga användare att visa.</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import axios from 'axios'
import UserListTable from '@/components/UserListTable.vue'
import { useUserListStore } from '@/stores/useUserStore'

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const userStore = useUserListStore()

const hasUsers = computed(() => userStore.users.length > 0)

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

onMounted(() => {
  fetchUsers()
})
</script>
