import { putRequest } from "../utils/api";
import { ADMIN_USERS_ENDPOINT } from "../utils/endpoints";

/**
 * {
 *      TODO
 * }
 */
export function editUser(userId, newUserData) {
    return putRequest(ADMIN_USERS_ENDPOINT + userId, newUserData);
}

export function editPassword(userId, passwordData) {
    return putRequest(
        ADMIN_USERS_ENDPOINT + userId + "/change_password/",
        passwordData
    );
}
