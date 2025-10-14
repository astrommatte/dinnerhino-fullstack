<template>
  <UserListTable
    @edit-user="openEditDialog"
    @delete-user="deleteUser"
    @edit-user-toggle-active="toggleActive"
  />

  <UserForm
    v-model="editDialogVisible"
    :userData="editUser"
    @save="saveEdit"
    @cancel="cancelEdit"
  />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import UserListTable from '@/components/UserListTable.vue'
import UserForm from '@/components/UserForm.vue'
import { useUserListStore } from '@/stores/useUserStore'
import { useToaster } from '@/stores/useToastStore'
import { useConfirmationStore } from '@/stores/useConfirmationStore'

const { showSuccessToast, showErrorToast } = useToaster()
const confirmationStore = useConfirmationStore()
const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const userStore = useUserListStore()

const editDialogVisible = ref(false)
const editUser = ref({})

function openEditDialog(user) {
  editUser.value = { ...user }
  editDialogVisible.value = true
}

function cancelEdit() {
  editDialogVisible.value = false
}

async function saveEdit(user) {
  try {
    await axios.put(`${apiUrl}/api/users/${user.id}`, user, {
      headers: { Authorization: localStorage.getItem('auth') }
    })
    showSuccessToast('Användare uppdaterad!')
    editDialogVisible.value = false
    await fetchUsers()
  } catch (err) {
    showErrorToast('Kunde inte uppdatera användaren.')
  }
}

async function deleteUser(user) {
  confirmationStore.confirmDeleteUser(user, async () => {
    try {
      await axios.delete(`${apiUrl}/api/users/${user.id}`, {
        headers: { Authorization: localStorage.getItem('auth') }
      })
      await fetchUsers()
      showSuccessToast('Användaren är nu borttagen!')
    } catch (err) {
      showErrorToast('Kunde inte ta bort användaren.')
    }
  })
}

async function toggleActive(user) {
  confirmationStore.confirm1(user, async () => {

  try {
    await axios.put(`${apiUrl}/api/users/${user.id}/active`, { active: !user.active }, {
      headers: { Authorization: localStorage.getItem('auth') }
    })

    showSuccessToast('Användaren uppdaterad!')
    await fetchUsers()
  } catch (err) {
    showErrorToast('Kunde inte uppdatera användaren.')
  }
  })
}

async function fetchUsers() {
  try {
    const res = await axios.get(`${apiUrl}/api/users`, {
      headers: { Authorization: localStorage.getItem('auth') }
    })
    userStore.setUserList(res.data)

  } catch (err) {
    showErrorToast('Gick inte att hämta användare')
  }
}

onMounted(() => {
  fetchUsers()
})
</script>
