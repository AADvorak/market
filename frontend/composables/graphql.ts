import type {GraphqlRequest} from "~/data/request";
import {GraphqlRequestSender} from "~/api/graphql-request-sender";
import {useUser} from "~/stores/user";

interface GraphqlParams<T> {
  request: GraphqlRequest
  successHandler?: () => void
  failHandler?: () => void
  dataHandler?: (data: T) => void
  graphqlErrorsHandler?: (errors: any[]) => void
  requestErrorHandler?: (error: any) => void
}

export async function useGraphql<T>(params: GraphqlParams<T>) {
  try {
    const response = await new GraphqlRequestSender(params.request).send()
    if (response.errors) {
      handleGraphqlErrors(response.errors, params)
    } else {
      successHandler<T>(response.data, params)
    }
  } catch (e: any) {
    if (e.status === 401) {
      useUser().clear()
      window.location.href = '/oauth2/authorization/external'
    } else {
      handleRequestError(e, params)
    }
  }
}

function handleGraphqlErrors<T>(errors: any[], params: GraphqlParams<T>) {
  if (params.graphqlErrorsHandler) {
    params.graphqlErrorsHandler(errors)
  } else if (params.failHandler) {
    params.failHandler()
  }
}

function handleRequestError<T>(e: any, params: GraphqlParams<T>) {
  if (params.requestErrorHandler) {
    params.requestErrorHandler(e)
  } else if (params.failHandler) {
    params.failHandler()
  }
}

function successHandler<T>(data: any, params: GraphqlParams<T>) {
  if (params.dataHandler) {
    if (data && data[params.request.name]) {
      params.dataHandler(<T>data[params.request.name])
    }
  } else if (params.successHandler) {
    params.successHandler()
  }
}
