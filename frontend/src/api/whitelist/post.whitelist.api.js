import { postRequest } from "../utils/api";
import {
    ADMIN_WHITELIST_ENDPOINT,
    WHITELIST_ENDPOINT
} from "../utils/endpoints";

const ACTIVATE_CID_ENDPOINT = "activate_cid/";

/**
 * {
 *     cids: [String]
 * }
 */
export function addUsersToWhitelist(whitelistData) {
    return postRequest(ADMIN_WHITELIST_ENDPOINT, whitelistData);
}

/**
 * {
 *     cid: String
 * }
 */
export function cidIsWhitelisted(whitelistData) {
    return postRequest(ADMIN_WHITELIST_ENDPOINT, whitelistData);
}

/**
 * {
 *      cid: String
 * }
 */
export function activateCid(whitelistData) {
    return postRequest(
        WHITELIST_ENDPOINT + ACTIVATE_CID_ENDPOINT,
        whitelistData,
        false
    );
}
