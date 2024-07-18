<template>
  <v-card width="100%">
    <v-card-title>Магазины</v-card-title>
    <v-card-text>
      <v-data-table-server
          v-model:items-per-page="itemsPerPage"
          :headers="headers"
          :items="shops.data"
          :items-length="shops.elements"
          :search="filter"
          item-value="name"
          @click:row="(_, row) => selectItem(row.item)"
          @update:options="onDataTableOptionsUpdate"
      >
        <template v-slot:tfoot>
          <div class="d-flex">
            <v-text-field
                :model-value="filter"
                density="compact"
                placeholder="Поиск"
                @update:model-value="setFilter"
                hide-details/>
          </div>
        </template>
      </v-data-table-server>
    </v-card-text>
  </v-card>
  <message-dialog ref="message"/>
</template>

<script setup>
import {useGraphql} from "~/composables/graphql";

const props = defineProps({
  initFilter: String,
  initCurrentPage: Number,
  excludeProductId: Number
})

const emits = defineEmits(['current-page', 'filter', 'selected-item'])

const
    filter = ref(''),
    currentPage = ref(1),
    shops = ref({
      elements: 0,
      pages: 0,
      data: []
    }),
    message = ref(null),
    itemsPerPage = ref(5),
    headers = ref([
      { title: 'id', key: 'id', align: 'start', sortable: false },
      { title: 'Название', key: 'name', align: 'start', sortable: false },
      { title: 'Описание', key: 'description', align: 'start', sortable: false },
    ])

watch(currentPage, () => {
  emits('current-page', currentPage.value)
  fetchShops()
})
watch(filter, () => {
  emits('filter', filter.value)
  fetchShops()
})
watch(itemsPerPage, () => {
  fetchShops()
})
watch(() => props.initFilter, (newValue) => filter.value = newValue)
watch(() => props.initCurrentPage, (newValue) => currentPage.value = newValue)

onMounted(() => {
  fetchShops()
})

function onDataTableOptionsUpdate(options) {
  itemsPerPage.value = options.itemsPerPage
  currentPage.value = options.page
}
async function fetchShops() {
  await useGraphql({
    request: buildShopsRequest(),
    dataHandler: data => shops.value = data,
    failHandler() {
      message.value?.show('Ошибка при загрузке магазинов')
    },
  })
}
function buildShopsRequest() {
  return {
    type: 'query',
    name: 'shops',
    variables: {
      filter: {
        value: filter.value,
        type: 'String'
      },
      excludeProductId: {
        value: props.excludeProductId || 0,
        type: 'Int'
      },
      page: {
        value: currentPage.value - 1,
        type: 'Int!'
      },
      size: {
        value: itemsPerPage.value,
        type: 'Int!'
      }
    },
    responseFields: ['elements', 'pages', {
      'data': ['id', 'name', 'description']
    }]
  }
}
function setFilter(value) {
  filter.value = value
  currentPage.value = 1
}
function selectItem(item) {
  emits('selected-item', item)
}
</script>
