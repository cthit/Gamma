import { deleteRequest } from "../utils/api";
import { ADMIN_WHITELIST_ENDPOINT } from "../utils/endpoints";

export function deleteWhitelistItem(whitelistId) {
  return deleteRequest(ADMIN_WHITELIST_ENDPOINT + whitelistId);
}
