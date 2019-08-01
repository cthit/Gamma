import { getRequest } from "../utils/api";
import { ADMIN_API_KEYS_ENDPOINT } from "../utils/endpoints";

export function getApiKeys() {
    return getRequest(ADMIN_API_KEYS_ENDPOINT, true, input => ({
        data: input.data.map(api => ({
            id: api.id,
            name: api.name,
            descriptionSv: api.description.sv,
            descriptionEn: api.description.en
        }))
    }));
}

export function getApiKey(apiKeyId) {
    return getRequest(ADMIN_API_KEYS_ENDPOINT + apiKeyId, true, ({ data }) => ({
        data: {
            id: data.id,
            name: data.name,
            descriptionSv: data.description.sv,
            descriptionEn: data.description.en
        }
    }));
}
