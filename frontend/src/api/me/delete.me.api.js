import { deleteRequest } from "../utils/api";
import { USERS_ENDPOINT } from "../utils/endpoints";

export function deleteMe(passwordData) {
    return deleteRequest(USERS_ENDPOINT + "me", passwordData);
}
