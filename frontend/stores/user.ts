import { defineStore } from 'pinia'
import {ApiProvider} from "~/api/api-provider";
import {User} from "~/data/model";

export const useUser = defineStore('user', {
  state: () => {
    return {
      user: User.empty()
    }
  },
  getters: {
    userRepresentingString: (state) => {
      const user = state.user
      if (user.firstName && user.lastName) {
        return user.firstName + ' ' + user.lastName
      }
      return user.email
    },
    isAdmin: (state) => {
      return state.user.roles.includes('ADMIN')
    }
  },
  actions: {
    async load() {
      const data = (await new ApiProvider({
        name: 'getUserInfo',
        responseFields: ['firstName', 'lastName', 'email', 'roles']
      }).sendRequest())?.getData()
      if (data) {
        this.user = data
      }
    },
    clear() {
      this.user = User.empty()
    }
  },
})
