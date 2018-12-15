import { getRequest } from "../utils/api";
import { ADMIN_ACTIVATION_CODES_ENDPOINT } from "../utils/endpoints";

export function getActivationCode(activationCodeId) {
    return getRequest(ADMIN_ACTIVATION_CODES_ENDPOINT + activationCodeId);
}

export function getActivationCodes() {
    return getRequest(ADMIN_ACTIVATION_CODES_ENDPOINT);
}
