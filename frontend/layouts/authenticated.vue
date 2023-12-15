<template>
  <v-app :theme="theme">
    <v-app-bar app>
      <v-btn to="/products" nuxt>Товары</v-btn>
      <v-btn to="/shops" nuxt>Магазины</v-btn>
      <v-switch hide-details v-model="darkMode" label="Темный режим"/>
      <v-spacer></v-spacer>
      <v-btn> {{ user.description }}</v-btn>
      <v-btn @click="logout">Выход</v-btn>
    </v-app-bar>
    <v-main>
      <slot/>
    </v-main>
  </v-app>
</template>

<script setup>
import {useUser} from "~/stores/user";
import {useDarkMode} from "~/composables/dark-mode";

const {darkMode, theme} = useDarkMode()
const user = useUser()

function logout() {
  user.clear()
  window.open(
      '/logout',
      '_self'
  )
}
</script>
