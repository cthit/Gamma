import { deleteRequest } from "../utils/api";
import { ACTIVATION_CODES_ENDPOINT } from "../utils/endpoints";

export function deleteActivationCode(activationCodeId) {
    return deleteRequest(ACTIVATION_CODES_ENDPOINT + activationCodeId);
}
