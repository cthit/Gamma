import { deleteRequest } from "../utils/api";
import { ADMIN_CLIENTS_ENDPOINT } from "../utils/endpoints";

export function deleteClient(clientUid) {
    return deleteRequest(ADMIN_CLIENTS_ENDPOINT + clientUid);
}
