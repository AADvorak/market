<template>
  <v-dialog
      :model-value="model.opened"
      :persistent="true"
      max-width="600px"
  >
    <v-card width="100%">
      <v-card-title>Сообщение</v-card-title>
      <v-card-text>
        <div class="d-flex">
          {{ model.text }}
        </div>
        <div class="d-flex mt-4">
          <v-btn @click="hide">
            OK
          </v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
const model = reactive({
  opened: false,
  text: ''
})

function show(text: string) {
  model.text = text
  model.opened = true
}

function hide() {
  model.opened = false
}

defineExpose({
  show
})
</script>
