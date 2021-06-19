import { putRequest } from "../utils/api";
import { GROUPS_ENDPOINT, USERS_ENDPOINT } from "../utils/endpoints";

export function uploadUserAvatar(file) {
    const data = new FormData();
    data.append("file", file);
    return putRequest(USERS_ENDPOINT + "avatar", data);
}

export function uploadGroupAvatar(file, groupId) {
    const data = new FormData();
    data.append("file", file);
    return putRequest(GROUPS_ENDPOINT + "avatar/" + groupId, data);
}

export function uploadGroupBanner(file, groupId) {
    const data = new FormData();
    data.append("file", file);
    return putRequest(GROUPS_ENDPOINT + "banner/" + groupId, data);
}
