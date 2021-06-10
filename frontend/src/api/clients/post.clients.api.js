import { postRequest } from "../utils/api";
import { ADMIN_CLIENTS_ENDPOINT } from "../utils/endpoints";

export function addClient(client) {
    return postRequest(ADMIN_CLIENTS_ENDPOINT, client);
}

export const resetClientSecret = clientId =>
    postRequest(ADMIN_CLIENTS_ENDPOINT + clientId + "/reset", null);
