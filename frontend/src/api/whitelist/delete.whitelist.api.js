import { deleteRequest } from "../utils/api";
import { ADMIN_WHITELIST_ENDPOINT } from "../utils/endpoints";

export function deleteWhitelistItem(whitelistCid) {
    return deleteRequest(ADMIN_WHITELIST_ENDPOINT + whitelistCid);
}
