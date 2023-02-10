import { deleteRequest } from "../utils/api";
import {
    ADMIN_USERS_ENDPOINT,
    GROUPS_ENDPOINT,
    USERS_ENDPOINT
} from "../utils/endpoints";

export function deleteGroupBanner(groupId) {
    return deleteRequest(GROUPS_ENDPOINT + "banner/" + groupId);
}

export function deleteGroupAvatar(groupId) {
    return deleteRequest(GROUPS_ENDPOINT + "avatar/" + groupId);
}

export function deleteUserAvatar(userId) {
    return deleteRequest(ADMIN_USERS_ENDPOINT + "avatar/" + userId);
}

export function deleteMeAvatar() {
    return deleteRequest(USERS_ENDPOINT + "me/avatar");
}
