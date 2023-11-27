import type {GraphqlRequest} from "~/data/request";
import {GraphqlRequestSender} from "~/api/graphql-request-sender";
import {useUser} from "~/stores/user";

export async function useGraphql(request: GraphqlRequest) {
  let data: any = null, errors: any[] = []

  try {
    const response = await new GraphqlRequestSender(request).send()
    if (response.errors) {
      errors = response.errors
    }
    if (response.data) {
      data = response.data[request.name]
    }
  } catch (e: any) {
    if (e.status === 401) {
      useUser().clear()
      window.location.href = '/oauth2/authorization/external'
    }
  }

  return {data, errors}
}
