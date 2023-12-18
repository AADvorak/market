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
              ref="priceField"
              type="number"
              step="10"
              min="0"
              label="Цена"
              :error="!!validation.price.length"
              :error-messages="validation.price"
              required/>
          <div class="d-flex">
            <v-btn type="submit" color="success" class="mr-4">
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
  <message-dialog ref="message"/>
</template>

<script setup lang="ts">
import {Price} from "~/data/model";
import {useGraphql} from "~/composables/graphql";
import {useFillValidation} from "~/composables/fill-validation";
import MessageDialog from "~/components/message-dialog.vue";

class PriceValidation {
  constructor(public price: string[], public shopId: string[]) {}

  static empty() {
    return new PriceValidation([], [])
  }
}

const props = defineProps<{
  productId: number
  editedPrice: Price
}>()

const emit = defineEmits(['price-saved'])

const
    opened = ref<boolean>(false),
    validation = ref<PriceValidation>(PriceValidation.empty()),
    modelPrice = ref<string>(''),
    message = ref<InstanceType<typeof MessageDialog> | null>(null),
    priceField = ref<HTMLInputElement | null>(null)

watch(priceField, () => {
  priceField.value?.focus()
})
watch(() => props.editedPrice, (newValue) => {
  if (newValue.price) {
    modelPrice.value = newValue.price.toString()
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
  props.editedPrice.price = parseFloat(modelPrice.value)
  await useGraphql({
    request: buildSavePriceRequest(),
    successHandler: hideAndFetchPrices,
    graphqlErrorsHandler: errors => {
      useFillValidation(validation.value, errors)
      if (validation.value.shopId.length) {
        message.value?.show('Цена для выбранного магазина уже задана')
        hideAndFetchPrices()
      }
    },
    failHandler() {
      message.value?.show('Ошибка при сохранении цены')
    },
  })
}
function buildSavePriceRequest() {
  const {id, shopId, price} = props.editedPrice
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
  return {
    name,
    type: 'mutation',
    variables: {
      input
    },
    responseFields
  }
}
function hideAndFetchPrices() {
  hide()
  emit('price-saved')
}
function resetValidation() {
  validation.value = PriceValidation.empty()
}
function show() {
  opened.value = true
}
function hide() {
  opened.value = false
}
</script>
