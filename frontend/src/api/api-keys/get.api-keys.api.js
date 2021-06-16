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
    return getRequest(ADMIN_API_KEYS_ENDPOINT + apiKeyId, ({ data }) => ({
        data: {
            id: data.id,
            prettyName: data.prettyName,
            descriptionSv: data.description != null ? data.description.sv : "",
            descriptionEn: data.description != null ? data.description.en : "",
            keyType: data.keyType
        }
    }));
}

export const getApiKeyTypes = () =>
    getRequest(ADMIN_API_KEYS_ENDPOINT + "types");
