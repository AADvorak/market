export interface GraphqlData<T> {
  [key: string]: T | null
}

export interface GraphqlError {
  message: string
  path?: string[]
  extensions: {
    field: string
    value?: string
    classification: string
  }
}

export interface GraphqlResponse<T> {
  data?: GraphqlData<T>
  errors?: GraphqlError[]
}
