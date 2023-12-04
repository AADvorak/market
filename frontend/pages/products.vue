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
              <th v-if="user.isAdmin" class="text-right"/>
            </tr>
            </thead>
            <tbody>
            <tr v-for="product in products.data" :key="product.id"
                style="cursor: pointer" @click="showProduct(product)">
              <td>{{ product.id }}</td>
              <td>{{ product.vendorCode }}</td>
              <td>{{ product.name }}</td>
              <td>{{ product.description }}</td>
              <td class="text-right" v-if="user.isAdmin">
                <v-icon @click.stop="askConfirmDeleteProduct(product)">
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
          <v-btn v-if="user.isAdmin" @click="addProduct">Добавить товар</v-btn>
        </v-card-text>
      </v-card>
      <confirm-dialog
          :config="confirmDialogConfig"
          @ok="deleteProduct"
          @cancel="cancelDeleteProduct"/>
      <message-dialog ref="message"/>
    </div>
  </NuxtLayout>
</template>

<script setup>
import {useUser} from "~/stores/user";
import {mdiDelete} from "@mdi/js";
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
    confirmDialogConfig = reactive({
      opened: false,
      text: ''
    }),
    productIdForDelete = ref(0),
    products = ref({
      elements: 0,
      pages: 0,
      data: []
    }),
    message = ref(null)

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
        value: 5,
        type: 'Int!'
      }
    },
    responseFields: ['elements', 'pages', {
      'data': ['id', 'vendorCode', 'name', 'description']
    }]
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
  confirmDialogConfig.text = `Удалить товар ${product.name}?`
  confirmDialogConfig.opened = true
}
async function deleteProduct() {
  confirmDialogConfig.opened = false
  await useGraphql({
    request: buildDeleteProductRequest(),
    successHandler: fetchProducts
  })
}
function buildDeleteProductRequest() {
  return {
    type: 'mutation',
    name: 'deleteProduct',
    variables: {
      id: {
        value: productIdForDelete.value,
        type: 'ID!'
      }
    }
  }
}
function cancelDeleteProduct() {
  confirmDialogConfig.opened = false
}
</script>
