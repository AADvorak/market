import {useUser} from "~/stores/user";

const REDIRECT_AUTHORIZED_URL = '/products'

export default defineNuxtRouteMiddleware(async (to) => {
  const user= useUser()
  if (user.user.email) {
    return
  } else {
    await user.load()
  }
  if (!user.user.email) {
    return abortNavigation()
  } else if (to.path !== REDIRECT_AUTHORIZED_URL) {
    return navigateTo(REDIRECT_AUTHORIZED_URL)
  }
})
