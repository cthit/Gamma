import { getRequest } from "../utils/api";
import { ADMIN_API_KEYS_ENDPOINT } from "../utils/endpoints";

export function getApiKeys() {
    return getRequest(ADMIN_API_KEYS_ENDPOINT);
}

export function getApiKey(apiKeyId) {
    return getRequest(ADMIN_API_KEYS_ENDPOINT + apiKeyId);
}
