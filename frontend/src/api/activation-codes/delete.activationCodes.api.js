import { deleteRequest } from "../utils/api";
import { ADMIN_ACTIVATION_CODES_ENDPOINT } from "../utils/endpoints";

export function deleteActivationCode(activationCodeId) {
    return deleteRequest(ADMIN_ACTIVATION_CODES_ENDPOINT + activationCodeId);
}
