<template>
  <NuxtLayout name="authenticated">
    <div class="d-flex align-center flex-column">
      <shop-table
          :init-current-page="currentPage"
          :init-filter="filter"
          @filter="setFilter"
          @currentPage="setCurrentPage"/>
    </div>
  </NuxtLayout>
</template>

<script setup>
import {usePageAndFilter} from "~/composables/page-and-filter";

const {
  filter,
  currentPage,
  setFilter,
  setCurrentPage,
  readUrlParams,
  setUrlParams
} = usePageAndFilter()

watch([currentPage, filter], () => {
  setUrlParams()
})

onMounted(() => {
  readUrlParams()
  setUrlParams()
})
</script>
