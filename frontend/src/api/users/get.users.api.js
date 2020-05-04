import { getRequest } from "../utils/api";
import { ADMIN_USERS_ENDPOINT, USERS_ENDPOINT } from "../utils/endpoints";

export function getUsersMinified() {
    return getRequest(USERS_ENDPOINT + "minified");
}

export function getUser(id) {
    return getRequest(USERS_ENDPOINT + id, response => {
        const user = response.data;
        if (user.phone == null) {
            user.phone = "";
        }
        return { data: user };
    });
}

export function getUserAdmin(id) {
    return getRequest(ADMIN_USERS_ENDPOINT + id, response => {
        const user = response.data;
        if (user.phone == null) {
            user.phone = "";
        }
        return { data: user };
    });
}
