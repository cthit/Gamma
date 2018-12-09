import { getRequest } from "../utils/api";
import { ADMIN_WHITELIST_ENDPOINT } from "../utils/endpoints";

export function getWhitelistItem(whitelistItemId) {
    return getRequest(ADMIN_WHITELIST_ENDPOINT + whitelistItemId);
}

export function getWhitelist() {
    return getRequest(ADMIN_WHITELIST_ENDPOINT);
}
