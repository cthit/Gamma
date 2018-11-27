import { postRequest } from "../utils/api";
import { ADMIN_GROUPS_ENDPOINT } from "../utils/endpoints";

/**
 * {
 *      TODO
 * }
 */
export function addGroup(groupData) {
    return postRequest(ADMIN_GROUPS_ENDPOINT, groupData);
}
