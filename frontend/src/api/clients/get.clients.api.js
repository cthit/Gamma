import { getRequest } from "../utils/api";
import { ADMIN_CLIENTS_ENDPOINT } from "../utils/endpoints";

export function getClients() {
    return getRequest(ADMIN_CLIENTS_ENDPOINT, true, input =>
        input.data.map(client => ({
            id: client.id,
            name: client.name,
            descriptionEn: client.description.en,
            descriptionSv: client.description.sv,
            webServerRedirectUri: client.webServerRedirectUri
        }))
    );
}

export function getClient(clientId) {
    return getRequest(ADMIN_CLIENTS_ENDPOINT + clientId, true, input => ({
        data: {
            id: input.data.id,
            name: input.data.additionalInformation.name,
            descriptionEn: input.data.additionalInformation.description.en,
            descriptionSv: input.data.additionalInformation.description.sv,
            webServerRedirectUri: input.data.webServerRedirectUri
        }
    }));
}
