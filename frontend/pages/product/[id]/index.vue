<template>
  <NuxtLayout name="authenticated">
    <div class="d-flex align-center flex-column">
      <v-container width="100%">
        <v-row class="mb-4">
          <v-col :cols="cols" :sm="cols" :md="cols">
            <v-card width="100%">
              <v-card-title>{{ product.name || "Новый товар" }} - информация</v-card-title>
              <v-card-text>
                <product-form :bus="bus"/>
              </v-card-text>
            </v-card>
          </v-col>
          <v-col v-if="!!product.id" cols="6" sm="6" md="6">
            <v-card width="100%">
              <v-card-title>Цены в магазинах</v-card-title>
              <v-card-text>
                <price-table
                    :product-id="product.id"
                    :bus="bus"/>
                <v-btn v-if="isAdmin" @click="showShopTable">
                  Добавить
                </v-btn>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
        <v-row>
          <shop-table
              v-if="shopTableOpened"
              :init-current-page="1"
              :exclude-product-id="product.id"
              :init-filter="String()"
              @selectedRow="addPrice"/>
        </v-row>
      </v-container>
      <price-editor :product-id="product.id" :bus="bus"/>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import {userInfo} from "~/stores/user-info";
import mitt from "mitt";
import type {Emitter} from "mitt";
import {Product, Shop} from "~/data/model";

const bus: Emitter<any> = mitt()
bus.on('product-loaded', productLoaded)
bus.on('product-saved', productSaved)

const
    product = ref<Product>(Product.empty()),
    shopTableOpened = ref<boolean>(false)

const
    isAdmin = computed<boolean>(() => userInfo().isAdmin),
    cols = computed<number>(() => product.value.id ? 6 : 12)

onMounted(() => {
  const productId: number = parseInt(<string>useRoute().params.id)
  if (productId) {
    bus.emit('fetch-product', productId)
  }
})
function productSaved(value: Product) {
  product.value = value
  // todo fix reload
  useRouter().push('/product/' + value.id)
}
function productLoaded(value: Product) {
  product.value = value
}
function addPrice(shop: Shop) {
  bus.emit('edit-price', {
    id: 0,
    shopId: shop.id,
    shopName: shop.name,
    price: 0
  })
  hideShopTable()
}
function showShopTable() {
  shopTableOpened.value = true
}
function hideShopTable() {
  shopTableOpened.value = false
}
</script>
