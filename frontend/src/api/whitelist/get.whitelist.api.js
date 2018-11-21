import { getRequest } from "../utils/api";
import { WHITELIST_ENDPOINT } from "../utils/endpoints";

export function getWhitelist() {
    return getRequest(WHITELIST_ENDPOINT);
}
