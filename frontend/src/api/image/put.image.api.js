import { putRequest } from "../utils/api";
import { USERS_ENDPOINT } from "../utils/endpoints";

export function uploadUserAvatar(file) {
    const data = new FormData();
    data.append("file", file);
    return putRequest(USERS_ENDPOINT + "me/avatar", data);
}
