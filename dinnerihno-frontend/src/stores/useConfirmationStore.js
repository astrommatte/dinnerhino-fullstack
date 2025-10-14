// stores/useConfirmationStore.js
import { defineStore } from 'pinia'
import { useConfirm } from 'primevue/useconfirm'



export const useConfirmationStore = defineStore('confirmation', () => {

const confirm = useConfirm()

const confirm1 = (onAccept, onReject = null) => {
    confirm.require({
        message: 'Vill du fortsätta?',
        header: 'Bekräfta',
        icon: 'pi pi-exclamation-triangle',
        rejectProps: {
            label: 'Avbryt',
            severity: 'secondary',
            outlined: true
        },
        acceptProps: {
            label: 'Ja'
        },
        accept: () => {
          onAccept?.()
        },
        reject: () => {
          onReject?.()
        }
    });
};

const confirm2 = (onAccept, onReject = null) => {
  confirm.require({
    message: `Är du säker på att du vill ta bort?`,
    header: 'Bekräfta borttagning',
    icon: 'pi pi-info-circle',
    rejectProps: {
      label: 'Avbryt',
      severity: 'secondary',
      outlined: true
    },
    acceptProps: {
      label: 'Ta bort',
      severity: 'danger'
    },
    accept: () => {
      onAccept?.()
    },
    reject: () => {
      onReject?.()
    }
  })
}

const confirmDeleteUser = (user, onAccept, onReject = null) => {
  confirm.require({
    message: `Är du säker på att du vill ta bort? ${user.username}`,
    header: 'Bekräfta borttagning',
    icon: 'pi pi-info-circle',
    rejectProps: {
      label: 'Avbryt',
      severity: 'secondary',
      outlined: true
    },
    acceptProps: {
      label: 'Ta bort',
      severity: 'danger'
    },
    accept: () => {
      onAccept?.()
    },
    reject: () => {
      onReject?.()
    }
  })
}

const confirmActivateToggleUser = (user, onAccept, onReject = null) => {
  confirm.require({
    message: `Är du säker på att du vill aktivera/inaktivera? ${user.username}`,
    header: 'Bekräfta aktivering/avaktivering',
    icon: 'pi pi-info-circle',
    rejectProps: {
      label: 'Avbryt',
      severity: 'secondary',
      outlined: true
    },
    acceptProps: {
      label: 'Ja',
    },
    accept: () => {
      onAccept?.()
    },
    reject: () => {
      onReject?.()
    }
  })
}

  return {
    confirm1,
    confirm2,
    confirmDeleteUser,
    confirmActivateToggleUser
  }
})
