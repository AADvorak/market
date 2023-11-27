import {GraphqlRequestSender} from "./graphql-request-sender";
import {useUser} from "~/stores/user";
import type {GraphqlRequest} from "~/data/request";

interface Validation {
  [index: string]: string[]
}

export class ApiProvider {

  _errors: any[] = []
  _data: any = null
  _name: string
  _sender: GraphqlRequestSender

  constructor(request: GraphqlRequest) {
    this._sender = new GraphqlRequestSender(request)
    this._name = request.name
  }

  async sendRequest() {
    try {
      const response = await this._sender.send()
      this._errors = response.errors || []
      if (response.data) {
        this._data = response.data[this._name]
      }
      return this
    } catch (e: any) {
      if (e.status === 401) {
        useUser().clear()
        window.location.href = '/oauth2/authorization/external'
      }
    }
  }

  getData() {
    return this._data
  }

  getErrors() {
    return this._errors
  }

  fillValidation(validation: Validation) {
    this._errors.filter(error => error.extensions.classification === 'BAD_REQUEST')
        .map(error => ({field: error.extensions.field, message: error.message}))
        .forEach(item => {
          if (!validation[item.field]) {
            validation[item.field] = []
          }
          validation[item.field].push(item.message)
        })
  }
}
