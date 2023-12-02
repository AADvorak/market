import type {GraphqlRequest} from "~/data/request";
import {GraphqlRequestSender} from "~/api/graphql-request-sender";
import {useUser} from "~/stores/user";

interface GraphqlParams<T> {
  request: GraphqlRequest
  successHandler?: () => void
  dataHandler?: (data: T) => void
  graphqlErrorsHandler?: (errors: any[]) => void
  requestErrorHandler?: (error: any) => void
}

export async function useGraphql<T>(params: GraphqlParams<T>) {
  try {
    const response = await new GraphqlRequestSender(params.request).send()
    if (response.errors && params.graphqlErrorsHandler) {
      params.graphqlErrorsHandler(response.errors)
    }
    if (response.data && response.data[params.request.name] && params.dataHandler) {
      params.dataHandler(<T>response.data[params.request.name])
    }
    if (!response.errors && params.successHandler) {
      params.successHandler()
    }
  } catch (e: any) {
    if (e.status === 401) {
      useUser().clear()
      window.location.href = '/oauth2/authorization/external'
    } else if (params.requestErrorHandler) {
      params.requestErrorHandler(e)
    }
  }
}
