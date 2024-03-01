import { deleteRequest } from "../utils/api";
import { ADMIN_API_KEYS_ENDPOINT } from "../utils/endpoints";

export function deleteApiKey(apiKeyId) {
    return deleteRequest(ADMIN_API_KEYS_ENDPOINT + apiKeyId);
}
