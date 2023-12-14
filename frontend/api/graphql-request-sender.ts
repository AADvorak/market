import type {GraphqlRequest, Variables, ResponseFieldsObject, ResponseFields} from "~/data/request";
import type {GraphqlResponse} from "~/data/response";

interface VariableValues {
  [index: string]: any
}

export class GraphqlRequestSender<T> {

  PATH = '/graphql'
  name: string
  type: string
  variables: Variables
  responseFields: ResponseFields

  constructor(request: GraphqlRequest) {
    this.name = request.name
    this.type = request.type ? request.type : 'query'
    this.variables = request.variables ? request.variables : {}
    this.responseFields = request.responseFields ? request.responseFields : []
  }

  async send(): Promise<GraphqlResponse<T>> {
    const query = this._makeQuery()
    const variables = this._makeVariablesValues()
    return await $fetch(this.PATH, {
      method: 'POST',
      body: {
        query,
        variables
      },
      headers: this._makeHeaders(),
      redirect: 'error'
    })
  }

  _makeHeaders() {
    return  {
      'Cache-Control': 'no-cache',
      'Content-Type': 'application/json',
    }
  }

  _makeQuery() {
    return `${this.type} ${this._makeVariablesWithTypesStr()} {
            ${this.name} ${this._makeVariablesStr()} ${this._makeResponseFieldsStr()} }`
  }

  _makeResponseFieldsStr(responseFields?: ResponseFields) {
    if (!responseFields) responseFields = this.responseFields
    let fieldsStr = ''
    for (const responseField of responseFields) {
      if (fieldsStr) fieldsStr += ', '
      fieldsStr += typeof responseField === 'string'
          ? responseField
          : this._makeResponseFieldsStrFromObject(responseField)
    }
    return fieldsStr ? `{ ${fieldsStr} }` : ''
  }

  _makeResponseFieldsStrFromObject(obj: ResponseFieldsObject) {
    let fieldsStr = ''
    for (const key in obj) {
      if (fieldsStr) fieldsStr += ', '
      fieldsStr += key + this._makeResponseFieldsStr(obj[key])
    }
    return fieldsStr
  }

  _makeVariablesStr() {
    let variablesStr = ''
    for (const key in this.variables) {
      if (variablesStr) variablesStr += ', '
      let valueStr = '$' + key
      variablesStr += `${key}: ${valueStr}`
    }
    return variablesStr ? `(${variablesStr})` : ''
  }

  _makeVariablesWithTypesStr() {
    let variablesStr = ''
    for (const key in this.variables) {
      if (variablesStr) variablesStr += ', '
      const keyStr = '$' + key
      const valueStr = this.variables[key].type
      variablesStr += `${keyStr}: ${valueStr}`
    }
    return variablesStr ? `(${variablesStr})` : ''
  }

  _makeVariablesValues() {
    const variablesValues: VariableValues = {}
    for (const key in this.variables) {
      variablesValues[key] = this.variables[key].value
    }
    return variablesValues
  }

}
