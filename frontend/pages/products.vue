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
            <tr v-for="p in products.data" @click="showProduct(p)">
              <td>{{ p.id }}</td>
              <td>{{ p.vendorCode }}</td>
              <td>{{ p.name }}</td>
              <td>{{ p.description }}</td>
              <td class="text-right" v-if="user.isAdmin">
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
          <v-btn v-if="user.isAdmin" @click="addProduct">Добавить товар</v-btn>
        </v-card-text>
      </v-card>
      <confirm-dialog
          :config="confirmDialogConfig"
          @ok="deleteProduct"
          @cancel="cancelDeleteProduct"/>
    </div>
  </NuxtLayout>
</template>

<script setup>
import {useUser} from "~/stores/user";
import {mdiDelete} from "@mdi/js";
import {usePageAndFilter} from "~/composables/page-and-filter";
import {useGraphql} from "~/composables/graphql";

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
    })

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
  const {data} = await useGraphql({
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
  })
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
  confirmDialogConfig.text = `Удалить товар ${product.name}?`
  confirmDialogConfig.opened = true
}
async function deleteProduct() {
  confirmDialogConfig.opened = false
  const {errors} = await useGraphql({
    type: 'mutation',
    name: 'deleteProduct',
    variables: {
      id: {
        value: productIdForDelete.value,
        type: 'ID!'
      }
    }
  })
  if (!errors.length) {
    await fetchProducts()
  }
}
function cancelDeleteProduct() {
  confirmDialogConfig.opened = false
}
</script>
