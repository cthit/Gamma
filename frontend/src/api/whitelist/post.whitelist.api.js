import { postRequest } from "../utils/api";
import {
    ADMIN_WHITELIST_ENDPOINT,
    WHITELIST_ENDPOINT
} from "../utils/endpoints";

const ACTIVATE_CID_ENDPOINT = "activate_cid/";

export function addUserToWhitelist(whitelistData) {
    return postRequest(ADMIN_WHITELIST_ENDPOINT, whitelistData);
}

export function activateCid(data) {
    return postRequest(WHITELIST_ENDPOINT + ACTIVATE_CID_ENDPOINT, data, false);
}
