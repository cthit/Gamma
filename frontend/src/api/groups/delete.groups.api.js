import { deleteRequest } from "../utils/api";
import { ADMIN_GROUPS_ENDPOINT } from "../utils/endpoints";

export function deleteGroup(groupId) {
    return deleteRequest(ADMIN_GROUPS_ENDPOINT + groupId);
}

export function removeUserFromGroup(groupId, userId) {
    return deleteRequest(
        ADMIN_GROUPS_ENDPOINT + groupId + "/members/" + userId
    );
}
