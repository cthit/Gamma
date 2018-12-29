import { deleteRequest } from "../utils/api";
import { ADMIN_USERS_ENDPOINT } from "../utils/endpoints";

export function deleteUser(userId) {
    return deleteRequest(ADMIN_USERS_ENDPOINT + userId);
}
