<template>
  <NuxtLayout/>
</template>

<script setup>
import {useUser} from "~/stores/user";

useUser().load().then(() => {
  useRouter().push("/products")
})
</script>
