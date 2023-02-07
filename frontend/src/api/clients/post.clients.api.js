import { postRequest } from "../utils/api";
import { ADMIN_CLIENTS_ENDPOINT } from "../utils/endpoints";

export function addClient(client) {
    return postRequest(ADMIN_CLIENTS_ENDPOINT, client);
}

export const resetClientSecret = clientUid =>
    postRequest(ADMIN_CLIENTS_ENDPOINT + clientUid + "/reset", null);
