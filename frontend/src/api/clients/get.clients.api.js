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
            id: input.data.id,
            name: input.data.additionalInformation.name,
            clientId: input.data.clientId,
            descriptionEn: input.data.additionalInformation.description.en,
            descriptionSv: input.data.additionalInformation.description.sv,
            webServerRedirectUri: input.data.webServerRedirectUri,
            autoApprove: input.data.autoApprove + ""
        }
    }));
}
