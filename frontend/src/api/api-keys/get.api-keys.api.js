import { getRequest } from "../utils/api";
import { ADMIN_API_KEYS_ENDPOINT } from "../utils/endpoints";

export function getApiKeys() {
    return getRequest(ADMIN_API_KEYS_ENDPOINT, input => ({
        data: input.data.map(api => ({
            id: api.id,
            prettyName: api.prettyName,
            descriptionSv: api.description != null ? api.description.sv : "",
            descriptionEn: api.description != null ? api.description.en : "",
            keyType: api.keyType
        }))
    }));
}

export function getApiKey(apiKeyId) {
    return getRequest(ADMIN_API_KEYS_ENDPOINT + apiKeyId);
}

export const getApiKeyTypes = () =>
    getRequest(ADMIN_API_KEYS_ENDPOINT + "types");
