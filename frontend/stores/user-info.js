import { defineStore } from 'pinia'
import {ApiProvider} from "~/api/api-provider";

export const userInfo = defineStore('userInfo', {
  state: () => {
    return {
      userInfo: {
        firstName: '',
        lastName: '',
        email: '',
        roles: []
      }
    }
  },
  getters: {
    userRepresentingString: (state) => {
      const userInfo = state.userInfo
      if (userInfo.firstName && userInfo.lastName) {
        return userInfo.firstName + ' ' + userInfo.lastName
      }
      return userInfo.email
    },
    isAdmin: (state) => {
      return state.userInfo.roles.includes('ADMIN')
    }
  },
  actions: {
    async loadUserInfo() {
      const data = (await new ApiProvider({
        name: 'getUserInfo',
        responseFields: ['firstName', 'lastName', 'email', 'roles'],
        router: useRouter()
      }).sendRequest()).getData()
      if (data) {
        this.userInfo = data
      }
    },
    clearUserInfo() {
      this.userInfo = {
        firstName: '',
        lastName: '',
        email: '',
        roles: []
      }
    }
  },
})
