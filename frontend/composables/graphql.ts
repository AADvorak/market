import type {GraphqlRequest} from "~/data/request";
import {GraphqlRequestSender} from "~/api/graphql-request-sender";
import {useUser} from "~/stores/user";
import type {GraphqlData, GraphqlError} from "~/data/response";

interface GraphqlParams<T> {
  request: GraphqlRequest
  successHandler?: () => void
  failHandler?: () => void
  dataHandler?: (data: T) => void
  graphqlErrorsHandler?: (errors: GraphqlError[]) => void
  requestErrorHandler?: (error: any) => void
}

export async function useGraphql<T>(params: GraphqlParams<T>) {
  try {
    const response = await new GraphqlRequestSender<T>(params.request).send()
    if (response.errors) {
      handleGraphqlErrors(response.errors, params)
    } else {
      handleSuccess<T>(response.data, params)
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

function handleGraphqlErrors<T>(errors: GraphqlError[], params: GraphqlParams<T>) {
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

function handleSuccess<T>(data: GraphqlData<T> | undefined, params: GraphqlParams<T>) {
  if (params.dataHandler) {
    if (data) {
      const entity = data[params.request.name]
      entity && params.dataHandler(entity)
    }
  } else if (params.successHandler) {
    params.successHandler()
  }
}
