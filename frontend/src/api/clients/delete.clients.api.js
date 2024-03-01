import { deleteRequest } from "../utils/api";
import { ADMIN_CLIENTS_ENDPOINT } from "../utils/endpoints";

export function deleteClient(clientId) {
    return deleteRequest(ADMIN_CLIENTS_ENDPOINT + clientId);
}
