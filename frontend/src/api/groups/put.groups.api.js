import { putRequest } from "../utils/api";
import { ADMIN_GROUPS_ENDPOINT } from "../utils/endpoints";

/**
 * {
 *      TODO
 * }
 */
export function editGroup(groupId, newGroupData) {
    return putRequest(ADMIN_GROUPS_ENDPOINT + groupId, newGroupData);
}
