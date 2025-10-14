// stores/useConfirmationStore.js
import { defineStore } from 'pinia'
import { useToaster } from './useToastStore';
import { useConfirm } from 'primevue/useconfirm'



export const useConfirmationStore = defineStore('confirmation', () => {
const { showSuccessToast, showErrorToast } = useToaster()
const confirm = useConfirm()

const confirm1 = () => {
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
            showSuccessToast('Du har accepterat!')
        },
        reject: () => {
            showErrorToast('Du har inte accepterat!')
        }
    });
};

const confirm2 = () => {
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
      showSuccessToast('Nu är det borttaget!')
    },
    reject: () => {
      showErrorToast('Borttagning avbröts.')
    }
  })
}

  return {
    confirm1,
    confirm2
  }
})
