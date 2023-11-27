<template>
  <v-app :theme="theme">
    <v-main>
      <slot />
    </v-main>
  </v-app>
</template>

<script setup>
import {useDarkMode} from "~/composables/dark-mode";

const {theme} = useDarkMode()
</script>
