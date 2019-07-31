import { USERS_ENDPOINT } from "../utils/endpoints";
import { getRequest } from "../utils/api";

export function getMe() {
    return getRequest(USERS_ENDPOINT + "me");
}
