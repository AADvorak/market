<template>
  <v-app :theme="theme">
    <v-app-bar app>
      <v-btn to="/products" nuxt>Товары</v-btn>
      <v-btn to="/shops" nuxt>Магазины</v-btn>
      <v-switch hide-details v-model="darkMode" label="Темный режим"/>
      <v-spacer></v-spacer>
      <v-btn> {{ userButtonText }}</v-btn>
      <v-btn @click="logout">Выход</v-btn>
    </v-app-bar>
    <v-main>
      <slot/>
    </v-main>
  </v-app>
</template>

<script>

import {userInfo} from "~/stores/user-info";

export default {
  data() {
    return {
      darkMode: localStorage.getItem('darkMode') === 'true'
    }
  },
  computed: {
    theme() {
      return this.darkMode ? 'dark' : 'light'
    },
    userButtonText() {
      return userInfo().userRepresentingString
    }
  },
  watch: {
    darkMode(newValue) {
      localStorage.setItem('darkMode', newValue)
    }
  },
  methods: {
    logout() {
      userInfo().clearUserInfo()
      window.open(
          '/logout',
          '_self'
      )
    }
  },
  mounted() {
    if (!userInfo().userInfo.email) {
      userInfo().loadUserInfo()
    }
  }
}
</script>
