import { putRequest } from "../utils/api";
import { USERS_ENDPOINT } from "../utils/endpoints";

export function editMe(newUserData) {
    return putRequest(USERS_ENDPOINT + "me", newUserData);
}

export function editPassword(passwordData) {
    return putRequest(USERS_ENDPOINT + "me/change_password", passwordData);
}

export const acceptUserAgreement = () =>
    putRequest(USERS_ENDPOINT + "me/accept-user-agreement");
