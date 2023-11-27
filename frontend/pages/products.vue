<template>
  <NuxtLayout name="authenticated">
    <div class="d-flex align-center flex-column">
      <v-card width="100%">
        <v-card-title>Товары</v-card-title>
        <v-card-text>
          <p>
            <span>Всего: страниц {{ products.pages }}, </span>
            <span>записей {{ products.elements }}</span>
          </p>
          <search-field :init-search-value="filter" @search="setFilter"/>
          <v-table>
            <thead>
            <tr>
              <th class="text-left">
                id
              </th>
              <th class="text-left">
                Артикул
              </th>
              <th class="text-left">
                Название
              </th>
              <th class="text-left">
                Описание
              </th>
              <th v-if="isAdmin" class="text-right"/>
            </tr>
            </thead>
            <tbody>
            <tr v-for="p in products.data" @click="showProduct(p)">
              <td>{{ p.id }}</td>
              <td>{{ p.vendorCode }}</td>
              <td>{{ p.name }}</td>
              <td>{{ p.description }}</td>
              <td class="text-right" v-if="isAdmin">
                <v-icon @click.stop="askConfirmDeleteProduct(p)">
                  {{ mdiDelete }}
                </v-icon>
              </td>
            </tr>
            </tbody>
          </v-table>
          <v-pagination
              v-model="currentPage"
              :length="products.pages"
          ></v-pagination>
          <v-btn v-if="isAdmin" @click="addProduct">Добавить товар</v-btn>
        </v-card-text>
      </v-card>
      <confirm-dialog
          :config="dialog"
          @ok="deleteProduct"
          @cancel="cancelDeleteProduct"/>
    </div>
  </NuxtLayout>
</template>

<script setup>
import {ApiProvider} from "~/api/api-provider";
import SearchField from "../components/search-field";
import {useUser} from "~/stores/user";
import {mdiDelete} from "@mdi/js";
import ConfirmDialog from "../components/confirm-dialog";
import {usePageAndFilter} from "~/components/page-and-filter";

const {
  filter,
  currentPage,
  setFilter,
  readUrlParams,
  setUrlParams
} = usePageAndFilter('/products')

const
    dialog = ref({
      opened: false,
      text: ''
    }),
    productIdForDelete = ref(0),
    products = ref({
      elements: 0,
      pages: 0,
      data: []
    })

const isAdmin = computed(() => useUser().isAdmin)

watch([currentPage, filter], () => {
  setUrlParams()
  fetchProducts()
})

onMounted(() => {
  readUrlParams()
  setUrlParams()
  fetchProducts()
})

async function fetchProducts() {
  const data = (await new ApiProvider({
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
        value: 5,
        type: 'Int!'
      }
    },
    responseFields: ['elements', 'pages', {
      'data': ['id', 'vendorCode', 'name', 'description']
    }]
  }).sendRequest()).getData()
  if (data) {
    products.value = data
  }
}
function showProduct(p) {
  useRouter().push('/product/' + p.id)
}
function addProduct() {
  useRouter().push('/product/0')
}
function askConfirmDeleteProduct(product) {
  productIdForDelete.value = product.id
  dialog.value.text = `Удалить товар ${product.name}?`
  dialog.value.opened = true
}
async function deleteProduct() {
  dialog.value.opened = false
  const errors = (await new ApiProvider({
    type: 'mutation',
    name: 'deleteProduct',
    variables: {
      id: {
        value: productIdForDelete.value,
        type: 'ID!'
      }
    }
  }).sendRequest()).getErrors()
  if (!errors.length) {
    await fetchProducts()
  }
}
function cancelDeleteProduct() {
  dialog.value.opened = false
}
</script>
