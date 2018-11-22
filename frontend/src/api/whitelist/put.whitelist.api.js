import { putRequest } from "../utils/api";
import { ADMIN_WHITELIST_ENDPOINT } from "../utils/endpoints";

/**
 * {
 *     cid: String
 * }
 */
export function editWhitelistItem(whitelistId, newWhitelistData) {
    return putRequest(ADMIN_WHITELIST_ENDPOINT + whitelistId, newWhitelistData);
}
