import { deleteRequest } from "../utils/api";
import { WHITELIST_ENDPOINT } from "../utils/endpoints";

export function deleteWhitelistItem(whitelistId) {
    return deleteRequest(WHITELIST_ENDPOINT + whitelistId);
}
