<template>
  <v-card width="100%">
    <v-card-title>Магазины</v-card-title>
    <v-card-text>
      <p>
        <span>Всего: страниц {{ shops.pages }}, </span>
        <span>записей {{ shops.elements }}</span>
      </p>
      <search-field :init-search-value="filter" @search="setFilter"/>
      <v-table>
        <thead>
        <tr>
          <th class="text-left">
            id
          </th>
          <th class="text-left">
            Название
          </th>
          <th class="text-left">
            Описание
          </th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="shop in shops.data" :key="shop.id"
            style="cursor: pointer" @click="selectRow(shop)">
          <td>{{ shop.id }}</td>
          <td>{{ shop.name }}</td>
          <td>{{ shop.description }}</td>
        </tr>
        </tbody>
      </v-table>
      <v-pagination
          v-model="currentPage"
          :length="shops.pages"/>
    </v-card-text>
  </v-card>
  <message-dialog ref="message"/>
</template>

<script setup>
import SearchField from "./search-field";
import {useGraphql} from "~/composables/graphql";

const props = defineProps({
  initFilter: String,
  initCurrentPage: Number,
  excludeProductId: Number
})

const emits = defineEmits(['current-page', 'filter', 'selected-row'])

const
    filter = ref(''),
    currentPage = ref(1),
    shops = ref({
      elements: 0,
      pages: 0,
      data: []
    }),
    message = ref(null)

watch(currentPage, () => {
  emits('current-page', currentPage.value)
  fetchShops()
})
watch(filter, () => {
  emits('filter', filter.value)
  fetchShops()
})
watch(() => props.initFilter, (newValue) => filter.value = newValue)
watch(() => props.initCurrentPage, (newValue) => currentPage.value = newValue)

onMounted(() => {
  fetchShops()
})

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
        value: 5,
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
function selectRow(row) {
  emits('selected-row', row)
}
</script>
