import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserListStore = defineStore('userListStore', () => {
  const users = ref([])

  const setUserList = (userList) => {
    users.value = userList
  }

  return {
    users,
    setUserList,
  }
})