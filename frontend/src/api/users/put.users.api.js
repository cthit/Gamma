import { putRequest } from "../utils/api";
import { USERS_ENDPOINT } from "../utils/endpoints";

/**
 * {
 *      TODO
 * }
 */
export function editUser(userId, newUserData) {
    return putRequest(USERS_ENDPOINT + userId, newUserData);
}
