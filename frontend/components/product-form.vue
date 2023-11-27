<template>
  <v-form @submit.prevent="saveProduct">
    <v-text-field
        v-model="product.vendorCode"
        label="Артикул"
        :disabled="isAdmin === false"
        :error="!!validation.vendorCode.length"
        :error-messages="validation.vendorCode"
        required/>
    <v-text-field
        v-model="product.name"
        label="Имя"
        :disabled="isAdmin === false"
        :error="!!validation.name.length"
        :error-messages="validation.name"
        required/>
    <v-text-field
        v-model="product.description"
        label="Описание"
        :disabled="isAdmin === false"
        :error="!!validation.description.length"
        :error-messages="validation.description"
        required/>
    <v-btn v-if="isAdmin" @click="saveProduct">
      Сохранить
    </v-btn>
  </v-form>
</template>

<script setup lang="ts">
import {ApiProvider} from "~/api/api-provider";
import {useUser} from "~/stores/user";
import {Product} from "~/data/model";
import type {Emitter} from "mitt";

class ProductValidation {
  constructor(
      public vendorCode: string[],
      public name: string[],
      public description: string[]) {
  }

  static empty() {
    return new ProductValidation([], [], [])
  }
}

const props = defineProps<{
  bus: Emitter<any>
}>()

props.bus.on('fetch-product', fetchProduct)

const
    validation = ref<ProductValidation>(ProductValidation.empty()),
    product = ref<Product>(Product.empty())

const isAdmin = computed<boolean>(() => useUser().isAdmin)

async function fetchProduct(productId: number) {
  const data = (await new ApiProvider({
    name: 'product',
    variables: {
      id: {
        value: productId,
        type: 'ID!'
      }
    },
    responseFields: ['id', 'vendorCode', 'name', 'description']
  }).sendRequest())?.getData()
  if (data) {
    product.value = data
    props.bus.emit('product-loaded', product.value)
  }
}

async function saveProduct() {
  resetValidation()
  if (product.value.id) {
    const provider = (await new ApiProvider({
      name: 'editProduct',
      type: 'mutation',
      variables: {
        input: {
          value: product.value,
          type: 'EditProductDtoRequest!'
        }
      },
      responseFields: []
    }).sendRequest())
    if (provider?.getErrors().length) {
      provider.fillValidation(validation.value)
    }
  } else {
    const provider = (await new ApiProvider({
      name: 'addProduct',
      type: 'mutation',
      variables: {
        input: {
          value: {
            vendorCode: product.value.vendorCode,
            name: product.value.name,
            description: product.value.description,
          },
          type: 'ProductDtoRequest!'
        }
      },
      responseFields: ['id', 'vendorCode', 'name', 'description']
    }).sendRequest())
    if (provider?.getErrors().length) {
      provider.fillValidation(validation.value)
    } else if (provider?.getData()) {
      product.value = provider.getData()
      props.bus.emit('product-saved', product.value)
    }
  }
}
function resetValidation() {
  validation.value = ProductValidation.empty()
}
</script>
