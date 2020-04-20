import { getRequest } from "../utils/api";
import { ADMIN_ACTIVATION_CODES_ENDPOINT } from "../utils/endpoints";

export function getActivationCode(activationCodeId) {
    return getRequest(
        ADMIN_ACTIVATION_CODES_ENDPOINT + activationCodeId,
        input => ({
            data: { ...input.data, createdAt: input.data.createdAt * 1000 } ///
        })
    );
}

export function getActivationCodes() {
    return getRequest(ADMIN_ACTIVATION_CODES_ENDPOINT, input =>
        input.data.map(i => ({
            ...i,
            createdAt: i.createdAt * 1000
        }))
    );
}
