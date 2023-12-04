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
  <message-dialog ref="message"/>
</template>

<script setup lang="ts">
import {useUser} from "~/stores/user";
import {Product} from "~/data/model";
import type {Emitter} from "mitt";
import {useGraphql} from "~/composables/graphql";
import {useFillValidation} from "~/composables/fill-validation";
import MessageDialog from "~/components/message-dialog.vue";

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
    product = ref<Product>(Product.empty()),
    message = ref<InstanceType<typeof MessageDialog> | null>(null)

async function fetchProduct(productId: number) {
  await useGraphql<Product>({
    request: buildProductRequest(productId),
    dataHandler: data => {
      product.value = data
      props.bus.emit('product-loaded', product.value)
    },
    failHandler() {
      message.value?.show('Ошибка при загрузке продукта')
    },
  })
}

function buildProductRequest(productId: number) {
  return {
    name: 'product',
    variables: {
      id: {
        value: productId,
        type: 'ID!'
      }
    },
    responseFields: ['id', 'vendorCode', 'name', 'description']
  }
}

async function saveProduct() {
  resetValidation()
  await useGraphql<Product>({
    request: product.value.id ? buildEditProductRequest() : buildAddProductRequest(),
    dataHandler: data => {
      product.value = data
      props.bus.emit('product-saved', product.value)
    },
    graphqlErrorsHandler: errors => useFillValidation(validation.value, errors),
    failHandler() {
      message.value?.show('Ошибка при сохранении продукта')
    },
  })
}

function buildEditProductRequest() {
  return {
    name: 'editProduct',
    type: 'mutation',
    variables: {
      input: {
        value: product.value,
        type: 'EditProductDtoRequest!'
      }
    },
    responseFields: []
  }
}

function buildAddProductRequest() {
  return {
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
  }
}

function resetValidation() {
  validation.value = ProductValidation.empty()
}
</script>
