import { postRequest } from "../utils/api";
import { RESET_PASSWORD_ENDPOINT } from "../utils/endpoints";

export function resetPasswordInitialize(data) {
    return postRequest(RESET_PASSWORD_ENDPOINT, data);
}

export function resetPasswordFinalize(data) {
    return postRequest(RESET_PASSWORD_ENDPOINT + "finish", data);
}
