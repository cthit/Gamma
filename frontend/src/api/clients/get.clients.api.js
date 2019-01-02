import { getRequest } from "../utils/api";
import { ADMIN_CLIENTS_ENDPOINT } from "../utils/endpoints";

export function getClients() {
    return getRequest(ADMIN_CLIENTS_ENDPOINT);
}

export function getClient(clientId) {
    return getRequest(ADMIN_CLIENTS_ENDPOINT + clientId);
}
