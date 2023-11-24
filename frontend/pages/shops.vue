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
import {usePageAndFilter} from "~/components/page-and-filter";

const {
  filter,
  currentPage,
  setFilter,
  setCurrentPage,
  readUrlParams,
  setUrlParams
} = usePageAndFilter('/shops')

watch([currentPage, filter], () => {
  setUrlParams()
})

onMounted(() => {
  readUrlParams()
  setUrlParams()
})
</script>
