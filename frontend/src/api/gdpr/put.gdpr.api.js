import { putRequest } from "../utils/api";
import { ADMIN_GDPR_ENDPOINT } from "../utils/endpoints";

export function setGDPRValue(userId, data) {
    return putRequest(ADMIN_GDPR_ENDPOINT + userId, data);
}
