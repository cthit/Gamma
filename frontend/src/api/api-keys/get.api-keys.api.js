import { getRequest } from "../utils/api";
import { ADMIN_API_KEYS_ENDPOINT } from "../utils/endpoints";

export function getApiKeys() {
    return getRequest(ADMIN_API_KEYS_ENDPOINT, input => ({
        data: input.data.map(api => ({
            id: api.id,
            name: api.name,
            descriptionSv: api.description != null ? api.description.sv : "",
            descriptionEn: api.description != null ? api.description.en : ""
        }))
    }));
}

export function getApiKey(apiKeyId) {
    return getRequest(ADMIN_API_KEYS_ENDPOINT + apiKeyId, ({ data }) => ({
        data: {
            id: data.id,
            name: data.name,
            descriptionSv: data.description != null ? data.description.sv : "",
            descriptionEn: data.description != null ? data.description.en : ""
        }
    }));
}
