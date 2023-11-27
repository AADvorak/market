interface Validation {
  [index: string]: string[]
}

export function useFillValidation(validation: Validation, errors: any[]) {
  errors.filter(error => error.extensions.classification === 'BAD_REQUEST')
      .map(error => ({field: error.extensions.field, message: error.message}))
      .forEach(item => {
        if (!validation[item.field]) {
          validation[item.field] = []
        }
        validation[item.field].push(item.message)
      })
}
