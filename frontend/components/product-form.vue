<template>
  <v-form @submit.prevent="saveProduct">
    <v-text-field
        v-model="product.vendorCode"
        label="Артикул"
        :disabled="!user.isAdmin"
        :error="!!validation.vendorCode.length"
        :error-messages="validation.vendorCode"
        required/>
    <v-text-field
        v-model="product.name"
        label="Имя"
        :disabled="!user.isAdmin"
        :error="!!validation.name.length"
        :error-messages="validation.name"
        required/>
    <v-text-field
        v-model="product.description"
        label="Описание"
        :disabled="!user.isAdmin"
        :error="!!validation.description.length"
        :error-messages="validation.description"
        required/>
    <v-btn v-if="user.isAdmin" @click="saveProduct">
      Сохранить
    </v-btn>
  </v-form>
</template>

<script setup lang="ts">
import {useUser} from "~/stores/user";
import {Product} from "~/data/model";
import type {Emitter} from "mitt";
import {useGraphql} from "~/composables/graphql";
import {useFillValidation} from "~/composables/fill-validation";

const user = useUser()

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

async function fetchProduct(productId: number) {
  const {data} = await useGraphql({
    name: 'product',
    variables: {
      id: {
        value: productId,
        type: 'ID!'
      }
    },
    responseFields: ['id', 'vendorCode', 'name', 'description']
  })
  if (data) {
    product.value = data
    props.bus.emit('product-loaded', product.value)
  }
}

async function saveProduct() {
  resetValidation()
  if (product.value.id) {
    const {errors} = await useGraphql({
      name: 'editProduct',
      type: 'mutation',
      variables: {
        input: {
          value: product.value,
          type: 'EditProductDtoRequest!'
        }
      },
      responseFields: []
    })
    if (errors.length) {
      useFillValidation(validation.value, errors)
    }
  } else {
    const {data, errors} = await useGraphql({
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
    })
    if (errors.length) {
      useFillValidation(validation.value, errors)
    } else if (data) {
      product.value = data
      props.bus.emit('product-saved', product.value)
    }
  }
}
function resetValidation() {
  validation.value = ProductValidation.empty()
}
</script>
