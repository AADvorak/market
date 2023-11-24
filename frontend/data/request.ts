export interface Variable {
  type: string
  value: any
}

export interface Variables {
  [index: string]: Variable
}

export interface ResponseFieldsObject {
  [index: string]: (string | ResponseFieldsObject)[]
}

export type ResponseFields = (string | ResponseFieldsObject)[]

export interface GraphqlRequest {
  name: string
  type?: string
  variables?: Variables
  responseFields?: ResponseFields
}
