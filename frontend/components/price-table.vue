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
    <tr v-for="p in prices.data" @click="editPrice(p)">
      <td>{{ p.shopName }}</td>
      <td>{{ p.price }}</td>
      <td class="text-right" v-if="user.isAdmin">
        <v-icon @click.stop="askConfirmDeletePrice(p)">
          {{ mdi.delete }}
        </v-icon>
      </td>
    </tr>
    </tbody>
  </v-table>
  <v-pagination
      v-model="currentPage"
      :length="prices.pages"/>
  <confirm-dialog
      :config="dialog"
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
    mdi = ref({
      delete: mdiDelete
    }),
    dialog = ref<DialogConfig>(DialogConfig.default()),
    priceIdForDelete = ref<number>(0),
    prices = ref<DataWithCounts<Price>>(DataWithCounts.empty()),
    currentPage = ref<number>(1)

watch([() => props.productId, currentPage], fetchPrices)

onMounted(fetchPrices)

function editPrice(price: Price) {
  if (user.isAdmin) {
    props.bus.emit('edit-price', price)
  }
}
async function fetchPrices() {
  const {data} = await useGraphql({
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
  })
  if (data) {
    prices.value = data
  }
}
function askConfirmDeletePrice(price: Price) {
  priceIdForDelete.value = price.id
  dialog.value.text = `Удалить цену товара в магазине ${price.shopName}?`
  dialog.value.opened = true
}
async function deletePrice() {
  dialog.value.opened = false
  const {errors} = await useGraphql({
    type: 'mutation',
    name: 'deletePrice',
    variables: {
      id: {
        value: priceIdForDelete.value,
        type: 'ID!'
      }
    }
  })
  if (!errors.length) {
    await fetchPrices()
  }
}
function cancelDeletePrice() {
  dialog.value.opened = false
}
</script>
