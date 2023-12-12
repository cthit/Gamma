import { putRequest } from "../utils/api";
import { ADMIN_USERS_ENDPOINT } from "../utils/endpoints";

export function editUser(userId, newUserData) {
    return putRequest(ADMIN_USERS_ENDPOINT + userId, newUserData);
}

export function editPasswordAdmin(userId, data) {
    return putRequest(ADMIN_USERS_ENDPOINT + userId + "/change_password", data);
}
