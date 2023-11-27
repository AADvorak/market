<template>
  <v-dialog
      :model-value="opened"
      :persistent="true"
      max-width="600px"
  >
    <v-card width="100%">
      <v-card-title>Выбор цены в магазине {{ editedPrice.shopName }}</v-card-title>
      <v-card-text>
        <v-form @submit.prevent="savePrice">
          <v-text-field
              v-model="modelPrice"
              type="number"
              step="10"
              min="0"
              label="Цена"
              :error="!!validation.price.length"
              :error-messages="validation.price"
              required/>
          <div class="d-flex">
            <v-btn color="success" class="mr-4" @click="savePrice">
              OK
            </v-btn>
            <v-btn @click="hide">
              Отмена
            </v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </v-dialog>
  <message :config="message" @hide="hideMessage"/>
</template>

<script setup lang="ts">
import type {Emitter} from "mitt";
import {Price} from "~/data/model";
import {DialogConfig} from "~/data/props";
import {useGraphql} from "~/composables/graphql";
import {useFillValidation} from "~/composables/fill-validation";

class PriceValidation {
  constructor(public price: string[], public shopId: string[]) {}

  static empty() {
    return new PriceValidation([], [])
  }
}

const props = defineProps<{
  productId: number,
  bus: Emitter<any>
}>()

const
    opened = ref<boolean>(false),
    validation = ref<PriceValidation>(PriceValidation.empty()),
    editedPrice = ref<Price>(Price.empty()),
    modelPrice = ref<string>(''),
    message = ref<DialogConfig>(DialogConfig.default())

props.bus.on('edit-price', (price: Price) => {
  editedPrice.value = price
  if (price.price) {
    modelPrice.value = price.price.toString()
  }
  resetValidation()
  show()
})

async function savePrice() {
  resetValidation()
  if (!modelPrice.value) {
    validation.value.price = ['укажите цену']
    return
  }
  editedPrice.value.price = parseFloat(modelPrice.value)
  const {id, shopId, price} = editedPrice.value
  let input, name, responseFields
  if (id) {
    input = {
      value: {
        id,
        price
      },
      type: 'EditPriceDtoRequest!'
    }
    name = 'editPrice'
  } else {
    input = {
      value: {
        shopId,
        productId: props.productId,
        price
      },
      type: 'PriceDtoRequest!'
    }
    name = 'addPrice'
    responseFields = ['id', 'shopId', 'shopName', 'price']
  }
  const {errors} = await useGraphql({
    name,
    type: 'mutation',
    variables: {
      input
    },
    responseFields
  })
  if (errors.length) {
    useFillValidation(validation.value, errors)
    if (validation.value.shopId.length) {
      showMessage('Цена для выбранного магазина уже задана')
      hideAndFetchPrices()
    }
  } else {
    hideAndFetchPrices()
  }
}
function hideAndFetchPrices() {
  hide()
  props.bus.emit('fetch-prices')
}
function resetValidation() {
  validation.value = PriceValidation.empty()
}
function showMessage(text: string) {
  message.value = {
    opened: true,
    text
  }
}
function hideMessage() {
  message.value.opened = false
}
function show() {
  opened.value = true
}
function hide() {
  opened.value = false
}
</script>
