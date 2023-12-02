<template>
  <v-dialog
      :model-value="config.opened"
      :persistent="true"
      max-width="600px"
  >
    <v-card width="100%">
      <v-card-title>Сообщение</v-card-title>
      <v-card-text>
        <div class="d-flex">
          {{ config.text }}
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
import {DialogConfig} from "~/data/props";

const config = reactive(DialogConfig.default())

const emits = defineEmits(['hide'])

function show(text: string) {
  config.text = text
  config.opened = true
}

function hide() {
  config.opened = false
  emits('hide')
}

defineExpose({
  show
})
</script>
