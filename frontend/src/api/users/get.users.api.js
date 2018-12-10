import { getRequest } from "../utils/api";
import { ADMIN_USERS_ENDPOINT, USERS_ENDPOINT } from "../utils/endpoints";

export function getUsersMinified(){
    return getRequest(USERS_ENDPOINT + "minified");
}

export function getUsers() {
    return getRequest(ADMIN_USERS_ENDPOINT);
}

export function getUser(cid) {
    return getRequest(ADMIN_USERS_ENDPOINT + cid);
}
