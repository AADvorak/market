<template>
  <v-dialog
      :model-value="config.opened"
      :persistent="true"
      max-width="600px"
  >
    <v-card width="100%">
      <v-card-title>Подтверждение</v-card-title>
      <v-card-text>
        <div class="d-flex mb-4">
          {{ config.text }}
        </div>
        <div class="d-flex">
          <v-btn color="success" class="mr-4" @click="ok">
            OK
          </v-btn>
          <v-btn @click="cancel">
            Отмена
          </v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type {DialogProps} from "~/data/props";

defineProps<DialogProps>()

const emits = defineEmits(['ok', 'cancel'])

function ok() {
  emits('ok')
}
function cancel() {
  emits('cancel')
}
</script>
