import { getRequest } from "../utils/api";
import { ADMIN_WHITELIST_ENDPOINT } from "../utils/endpoints";

export function getWhitelistItem(whitelistItemId) {
    return getRequest(ADMIN_WHITELIST_ENDPOINT + whitelistItemId);
}

// Gets returned as an array of cids.
// DigitCrud likes
export function getWhitelist() {
    return getRequest(ADMIN_WHITELIST_ENDPOINT, response => response.data.map(cid => ({ cid }));
    );
}
