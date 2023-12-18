<template>
  <div class="d-flex align-center flex-column">
    <v-container width="100%">
      <v-row class="mb-4">
        <v-col :cols="cols" :sm="cols" :md="cols">
          <v-card width="100%">
            <v-card-title>{{ product.name || "Новый товар" }} - информация</v-card-title>
            <v-card-text>
              <product-form
                  :product-id="productId"
                  @product-loaded="productLoaded"
                  @product-saved="productSaved"/>
            </v-card-text>
          </v-card>
        </v-col>
        <v-col v-if="!!product.id" cols="6" sm="6" md="6">
          <v-card width="100%">
            <v-card-title>Цены в магазинах</v-card-title>
            <v-card-text>
              <price-table
                  ref="priceTable"
                  :product-id="product.id"
                  @edit-price="price => editedPrice = price"/>
              <v-btn v-if="user.isAdmin" @click="showShopTable">
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
    <price-editor
        :product-id="productId"
        :edited-price="editedPrice"
        @price-saved="() => priceTable?.fetchPrices()"/>
  </div>
</template>

<script setup lang="ts">
import {useUser} from "~/stores/user";
import {Price, Product, Shop} from "~/data/model";
import PriceTable from "~/components/price-table.vue";

const user = useUser()

const
    productId = ref(0),
    product = ref<Product>(Product.empty()),
    editedPrice = ref(Price.empty()),
    shopTableOpened = ref<boolean>(false),
    priceTable = ref<InstanceType<typeof PriceTable> | null>(null)

const
    cols = computed<number>(() => product.value.id ? 6 : 12)

onMounted(() => {
  productId.value = parseInt(<string>useRoute().params.id)
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
  editedPrice.value = {
    id: 0,
    shopId: shop.id,
    shopName: shop.name,
    price: 0
  }
  hideShopTable()
}
function showShopTable() {
  shopTableOpened.value = true
}
function hideShopTable() {
  shopTableOpened.value = false
}
</script>
