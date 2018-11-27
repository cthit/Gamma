import { deleteRequest } from "../utils/api";
import { ADMIN_GROUPS_ENDPOINT } from "../utils/endpoints";

export function deleteGroup(groupId) {
    return deleteRequest(ADMIN_GROUPS_ENDPOINT + groupId);
}
