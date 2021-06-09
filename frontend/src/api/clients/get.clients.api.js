import { getRequest } from "../utils/api";
import { ADMIN_CLIENTS_ENDPOINT } from "../utils/endpoints";

export function getClients() {
    return getRequest(ADMIN_CLIENTS_ENDPOINT, input =>
        input.data.map(client => ({
            id: client.id,
            name: client.name,
            clientId: client.clientId,
            descriptionEn: client.description.en,
            descriptionSv: client.description.sv,
            webServerRedirectUri: client.webServerRedirectUri,
            autoApprove: client.autoApprove + ""
        }))
    );
}

export function getClient(clientId) {
    return getRequest(ADMIN_CLIENTS_ENDPOINT + clientId, input => ({
        data: {
            name: input.data.name,
            clientId: input.data.clientId,
            descriptionEn: input.data.description.en,
            descriptionSv: input.data.description.sv,
            webServerRedirectUri: input.data.webServerRedirectUri,
            autoApprove: input.data.autoApprove + ""
        }
    }));
}
