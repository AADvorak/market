<template>
  <v-table>
    <thead>
    <tr>
      <th class="text-left">
        Магазин
      </th>
      <th class="text-left">
        Цена
      </th>
      <th v-if="user.isAdmin" class="text-left"/>
    </tr>
    </thead>
    <tbody>
    <tr v-for="price in prices.data" :key="price.id" style="cursor: pointer" @click="editPrice(price)">
      <td>{{ price.shopName }}</td>
      <td>{{ price.price }}</td>
      <td class="text-right" v-if="user.isAdmin">
        <v-icon @click.stop="askConfirmDeletePrice(price)">
          {{ mdiDelete }}
        </v-icon>
      </td>
    </tr>
    </tbody>
  </v-table>
  <v-pagination
      v-model="currentPage"
      :length="prices.pages"/>
  <confirm-dialog
      :config="confirmDialogConfig"
      @ok="deletePrice"
      @cancel="cancelDeletePrice"/>
</template>

<script setup lang="ts">
import {useUser} from "~/stores/user";
import {mdiDelete} from "@mdi/js";
import type {Emitter} from "mitt";
import {DataWithCounts, Price} from "~/data/model";
import {DialogConfig} from "~/data/props";
import {useGraphql} from "~/composables/graphql";

const user = useUser()

const props = defineProps<{
  productId: number
  bus: Emitter<any>
}>()

props.bus.on('fetch-prices', fetchPrices)

const
    priceIdForDelete = ref<number>(0),
    currentPage = ref<number>(1),
    prices = ref<DataWithCounts<Price>>(DataWithCounts.empty()),
    confirmDialogConfig = reactive<DialogConfig>(DialogConfig.default())

watch([() => props.productId, currentPage], fetchPrices)

onMounted(fetchPrices)

function editPrice(price: Price) {
  if (user.isAdmin) {
    props.bus.emit('edit-price', price)
  }
}
async function fetchPrices() {
  await useGraphql<DataWithCounts<Price>>({
    request: buildPricesRequest(),
    dataHandler: data => prices.value = data
  })
}
function buildPricesRequest() {
  return {
    name: 'prices',
    variables: {
      productId: {
        value: props.productId,
        type: 'ID!'
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
      'data': ['id', 'shopId', 'shopName', 'price']
    }]
  }
}
function askConfirmDeletePrice(price: Price) {
  priceIdForDelete.value = price.id
  confirmDialogConfig.text = `Удалить цену товара в магазине ${price.shopName}?`
  confirmDialogConfig.opened = true
}
async function deletePrice() {
  confirmDialogConfig.opened = false
  await useGraphql({
    request: buildDeletePriceRequest(),
    successHandler: fetchPrices
  })
}
function buildDeletePriceRequest() {
  return {
    type: 'mutation',
    name: 'deletePrice',
    variables: {
      id: {
        value: priceIdForDelete.value,
        type: 'ID!'
      }
    }
  }
}
function cancelDeletePrice() {
  confirmDialogConfig.opened = false
}
</script>
