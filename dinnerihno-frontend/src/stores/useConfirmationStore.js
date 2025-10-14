// stores/useConfirmationStore.js
import { defineStore } from 'pinia'
import { useToaster } from './useToastStore';
import { useConfirm } from 'primevue/useconfirm'



export const useConfirmationStore = defineStore('confirmation', () => {
const { showSuccessToast, showErrorToast } = useToaster()
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
          showSuccessToast('Du har accepterat!')
        },
        reject: () => {
          onReject?.()
          showErrorToast('Du har inte accepterat!')
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
      showSuccessToast('Nu är det borttaget!')
    },
    reject: () => {
      onReject?.()
      showErrorToast('Borttagning avbröts.')
    }
  })
}

  return {
    confirm1,
    confirm2
  }
})
