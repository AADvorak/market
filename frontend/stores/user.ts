import {defineStore} from 'pinia'
import {User} from "~/data/model";
import {useGraphql} from "~/composables/graphql"

export const useUser = defineStore('user', {
  state: () => {
    return {
      user: User.empty()
    }
  },
  getters: {
    description: (state) => {
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
      await useGraphql<User>({
        request: {
          name: 'getUserInfo',
          responseFields: ['firstName', 'lastName', 'email', 'roles']
        },
        dataHandler: data => this.user = data
      })
    },
    clear() {
      this.user = User.empty()
    }
  },
})
