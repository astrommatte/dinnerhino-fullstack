<template>
  <DataTable :value="sortedUserList" responsiveLayout="scroll" class="p-datatable-sm">
    <Column field="firstName" header="Förnamn" />
    <Column field="lastName" header="Efternamn" />
    <Column field="email" header="E-post" />

    <Column header="Redigera">
      <template #body="slotProps">
        <Button
          icon="pi pi-pencil"
          class="p-button-rounded p-button-text"
          @click="$emit('edit-user', slotProps.data)"
        />
      </template>
    </Column>

    <Column header="Ta bort">
      <template #body="slotProps">
        <Button
          icon="pi pi-trash"
          class="p-button-rounded p-button-danger p-button-text"
          @click="$emit('delete-user', slotProps.data)"
        />
      </template>
    </Column>
  </DataTable>
</template>

<script setup>
import { computed } from 'vue'
import { useUserListStore } from '@/stores/useUserStore'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'

// Emits
defineEmits(['edit-user', 'delete-user'])

const userStore = useUserListStore()

const sortedUserList = computed(() => {
  return [...userStore.users].sort((a, b) =>
    a.lastName.localeCompare(b.lastName, 'sv')
  )
})
</script>
