import { putRequest } from "../utils/api";
import { RESET_PASSWORD_ENDPOINT } from "../utils/endpoints";

export function resetPasswordFinalize(data) {
    return putRequest(RESET_PASSWORD_ENDPOINT + "finish", data, false);
}
