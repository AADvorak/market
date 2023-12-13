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
  <confirm-dialog ref="confirm"/>
  <message-dialog ref="message"/>
</template>

<script setup lang="ts">
import {useUser} from "~/stores/user";
import {mdiDelete} from "@mdi/js";
import type {Emitter} from "mitt";
import {DataWithCounts, Price} from "~/data/model";
import {useGraphql} from "~/composables/graphql";
import MessageDialog from "~/components/message-dialog.vue";
import ConfirmDialog from "~/components/confirm-dialog.vue";

const user = useUser()

const props = defineProps<{
  productId: number
  bus: Emitter<any>
}>()

props.bus.on('fetch-prices', fetchPrices)

const
    currentPage = ref<number>(1),
    prices = ref<DataWithCounts<Price>>(DataWithCounts.empty()),
    message = ref<InstanceType<typeof MessageDialog> | null>(null),
    confirm = ref<InstanceType<typeof ConfirmDialog> | null>(null)

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
    dataHandler: data => prices.value = data,
    failHandler() {
      message.value?.show('Ошибка при загрузке цен')
    },
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
  confirm.value?.show(`Удалить цену товара в магазине ${price.shopName}?`, () => deletePrice(price.id))
}
async function deletePrice(priceId: number) {
  await useGraphql({
    request: buildDeletePriceRequest(priceId),
    successHandler: fetchPrices,
    failHandler() {
      message.value?.show('Ошибка при удалении цены')
    },
  })
}
function buildDeletePriceRequest(priceId: number) {
  return {
    type: 'mutation',
    name: 'deletePrice',
    variables: {
      id: {
        value: priceId,
        type: 'ID!'
      }
    }
  }
}
</script>
