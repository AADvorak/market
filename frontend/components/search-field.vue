<template>
  <v-form @submit.prevent="emitSearch">
    <div class="d-flex">
      <v-text-field
          v-model="search"
          label="Поиск"/>
    </div>
  </v-form>
</template>

<script setup>
const props = defineProps({
  initSearchValue: String
})

const emit = defineEmits(['search'])

const search = ref('')

watch(() => props.initSearchValue, (newValue) => search.value = newValue)

function emitSearch() {
  emit('search', search.value)
}
</script>
