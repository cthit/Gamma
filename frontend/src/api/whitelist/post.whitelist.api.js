import { postRequest } from "../utils/api";
import { WHITELIST_ENDPOINT } from "../utils/endpoints";

/**
 * {
 *     cid: String
 * }
 */
export function addUserToWhitelist(whitelistData) {
    return addUsersToWhitelist({ cids: [whitelistData.cid] });
}

/**
 * {
 *     cids: [String]
 * }
 */
export function addUsersToWhitelist(whitelistData) {
    return postRequest(WHITELIST_ENDPOINT, whitelistData);
}

/**
 * {
 *     cid: String
 * }
 */
export function cidIsWhitelisted(whitelistData) {
    return postRequest(WHITELIST_ENDPOINT, whitelistData);
}
