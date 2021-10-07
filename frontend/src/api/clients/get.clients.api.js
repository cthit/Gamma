import { getRequest } from "../utils/api";
import { ADMIN_CLIENTS_ENDPOINT } from "../utils/endpoints";

export function getClients() {
    return getRequest(ADMIN_CLIENTS_ENDPOINT, input =>
        input.data.map(client => ({
            ...client,
            autoApprove: client.autoApprove + ""
        }))
    );
}

export function getClient(clientId) {
    return getRequest(ADMIN_CLIENTS_ENDPOINT + clientId, input => ({
        data: {
            ...input.data,
            autoApprove: input.data.autoApprove + ""
        }
    }));
}
