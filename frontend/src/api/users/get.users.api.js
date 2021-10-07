import { getRequest } from "../utils/api";
import { ADMIN_USERS_ENDPOINT, USERS_ENDPOINT } from "../utils/endpoints";

export function getUsersMinified() {
    return getRequest(USERS_ENDPOINT);
}

export function getUser(id) {
    return getRequest(USERS_ENDPOINT + id, response => {
        const user = response.data;
        return { data: { ...user.user, groups: user.groups } };
    });
}

export function getUserAdmin(id) {
    return getRequest(ADMIN_USERS_ENDPOINT + id, response => {
        const user = response.data;
        return { data: { ...user.user, groups: user.groups } };
    });
}
