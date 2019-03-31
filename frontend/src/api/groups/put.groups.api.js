import { putRequest } from "../utils/api";
import { ADMIN_GROUPS_ENDPOINT } from "../utils/endpoints";

export function editGroup(groupId, newGroupData) {
    return putRequest(ADMIN_GROUPS_ENDPOINT + groupId, newGroupData);
}

export function editUserInGroup(groupId, userId, memberData) {
    return putRequest(
        ADMIN_GROUPS_ENDPOINT + groupId + "/members/" + userId,
        memberData
    );
}
