<template>
  <v-dialog
      :model-value="model.opened"
      :persistent="true"
      max-width="600px"
  >
    <v-card width="100%">
      <v-card-title>Подтверждение</v-card-title>
      <v-card-text>
        <div class="d-flex mb-4">
          {{ model.text }}
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
const model = reactive({
  opened: false,
  text: '',
  okCallback: () => {}
})

function show(text: string, okCallback: () => void) {
  model.okCallback = okCallback;
  model.text = text
  model.opened = true
}

function ok() {
  model.opened = false
  model.okCallback()
}

function cancel() {
  model.opened = false
}

defineExpose({
  show
})
</script>
