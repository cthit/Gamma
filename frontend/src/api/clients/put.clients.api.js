import { putRequest } from "../utils/api";
import { ADMIN_CLIENTS_ENDPOINT } from "../utils/endpoints";

export function editClient(clientId, client) {
    return putRequest(ADMIN_CLIENTS_ENDPOINT + clientId, client);
}
