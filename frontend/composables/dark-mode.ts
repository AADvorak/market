export function useDarkMode() {
  const TRUE = 'true', FALSE = 'false'
  const darkMode = ref<boolean>(localStorage.getItem('darkMode') === TRUE)
  const theme = computed<string>(() => darkMode.value ? 'dark' : 'light')
  watch(darkMode, (newValue) => localStorage.setItem('darkMode', newValue ? TRUE : FALSE))
  return {darkMode, theme}
}
