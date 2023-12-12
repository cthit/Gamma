import { postRequest } from "../utils/api";
import { ADMIN_GROUPS_ENDPOINT } from "../utils/endpoints";

export function addGroup(groupData) {
    return postRequest(ADMIN_GROUPS_ENDPOINT, groupData);
}

export function addUserToGroup(groupId, memberData) {
    return postRequest(
        ADMIN_GROUPS_ENDPOINT + groupId + "/members",
        memberData
    );
}
