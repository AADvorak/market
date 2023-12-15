import {useUser} from "~/stores/user";

export default defineNuxtRouteMiddleware(async (to) => {
  const user= useUser()
  if (!user.user.email) {
    await user.load()
  }
  if (!user.user.email) {
    return abortNavigation()
  } else if (to.path === '/') {
    return navigateTo('/products')
  }
})
