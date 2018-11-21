import { putRequest } from "../utils/api";
import { WHITELIST_ENDPOINT } from "../utils/endpoints";

/**
 * {
 *     cid: String
 * }
 */
export function editWhitelistItem(whitelistId, newWhitelistData) {
    return putRequest(WHITELIST_ENDPOINT + whitelistId, newWhitelistData);
}
