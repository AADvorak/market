<template>
  <div class="d-flex align-center flex-column">
    <v-card width="100%">
      <v-card-title>Товары</v-card-title>
      <v-card-text>
        <v-data-table-server
            v-model:items-per-page="itemsPerPage"
            :headers="headers"
            :items="products.data"
            :items-length="products.elements"
            :search="filter"
            item-value="name"
            items-per-page-text="Размер страницы"
            @update:options="onDataTableOptionsUpdate"
        >
          <template v-slot:tfoot>
            <div class="d-flex">
              <v-btn v-if="user.isAdmin" @click="addProduct">Добавить товар</v-btn>
              <v-text-field
                  :model-value="filter"
                  class="ml-2"
                  density="compact"
                  placeholder="Поиск"
                  @update:model-value="setFilter"
                  hide-details/>
            </div>
          </template>
          <template v-slot:item.actions="{ item }">
            <v-btn variant="text" @click.stop="showProduct(item.id)">
              <v-icon>{{ user.isAdmin ? mdiPencil : mdiEye }}</v-icon>
            </v-btn>
            <v-btn v-if="user.isAdmin" variant="text" @click.stop="askConfirmDeleteProduct(item)">
              <v-icon>{{ mdiDelete }}</v-icon>
            </v-btn>
          </template>
        </v-data-table-server>
      </v-card-text>
    </v-card>
    <confirm-dialog ref="confirm"/>
    <message-dialog ref="message"/>
  </div>
</template>

<script setup>
import {useUser} from "~/stores/user";
import {mdiDelete, mdiPencil, mdiEye} from "@mdi/js";
import {usePageAndFilter} from "~/composables/page-and-filter";
import {useGraphql} from "~/composables/graphql";
import MessageDialog from "~/components/message-dialog.vue";

const user = useUser()

const {
  filter,
  currentPage,
  setFilter,
  readUrlParams,
  setUrlParams
} = usePageAndFilter()

const
    products = ref({
      elements: 0,
      pages: 0,
      data: []
    }),
    message = ref(null),
    confirm = ref(null),
    headers = ref([
      { title: 'id', key: 'id', align: 'start', sortable: false },
      { title: 'Артикул', key: 'vendorCode', align: 'start', sortable: false },
      { title: 'Название', key: 'name', align: 'start', sortable: false },
      { title: 'Описание', key: 'description', align: 'start', sortable: false },
      { key: 'actions', sortable: false },
    ]),
    itemsPerPage = ref(5)

watch([currentPage, filter, itemsPerPage], () => {
  setUrlParams()
  fetchProducts()
})

onMounted(() => {
  readUrlParams()
  setUrlParams()
  fetchProducts()
})

function onDataTableOptionsUpdate(options) {
  itemsPerPage.value = options.itemsPerPage
  currentPage.value = options.page
}

async function fetchProducts() {
  await useGraphql({
    request: buildProductsRequest(),
    dataHandler: data => products.value = data,
    failHandler() {
      message.value?.show('Ошибка при загрузке продуктов')
    }
  })
}
function buildProductsRequest() {
  return {
    type: 'query',
    name: 'products',
    variables: {
      filter: {
        value: filter.value,
        type: 'String'
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
      'data': ['id', 'vendorCode', 'name', 'description']
    }]
  }
}
function showProduct(id) {
  useRouter().push('/product/' + id)
}
function addProduct() {
  useRouter().push('/product/0')
}
function askConfirmDeleteProduct(product) {
  confirm.value?.show(`Удалить товар ${product.name}?`, () => deleteProduct(product.id))
}
async function deleteProduct(productId) {
  await useGraphql({
    request: buildDeleteProductRequest(productId),
    successHandler: fetchProducts
  })
}
function buildDeleteProductRequest(productId) {
  return {
    type: 'mutation',
    name: 'deleteProduct',
    variables: {
      id: {
        value: productId,
        type: 'ID!'
      }
    }
  }
}
</script>
