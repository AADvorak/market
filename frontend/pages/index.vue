<template>
  <NuxtLayout/>
</template>

<script setup>
import {userInfo} from "~/stores/user-info";

userInfo().loadUserInfo().then(() => {
  useRouter().push("/products")
})
</script>
