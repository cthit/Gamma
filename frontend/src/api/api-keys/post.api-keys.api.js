import { postRequest } from "../utils/api";
import { ADMIN_API_KEYS_ENDPOINT } from "../utils/endpoints";

export function addApiKey(apiKey) {
    return postRequest(ADMIN_API_KEYS_ENDPOINT, apiKey);
}

export const resetApiKey = id =>
    postRequest(ADMIN_API_KEYS_ENDPOINT + id + "/reset");
