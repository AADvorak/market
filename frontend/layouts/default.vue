<template>
  <v-app :theme="theme">
    <v-main>
      <slot />
    </v-main>
  </v-app>
</template>

<script>
export default {
  data() {
    return {
      darkMode: localStorage.getItem('darkMode') === 'true'
    }
  },
  computed: {
    theme() {
      return this.darkMode ? 'dark' : 'light'
    }
  },
}
</script>
