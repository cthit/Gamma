import { getRequest } from "../utils/api";
import { ADMIN_GDPR_ENDPOINT } from "../utils/endpoints";

export function getUsersWithGDPRMinified() {
    return getRequest(ADMIN_GDPR_ENDPOINT + "minified");
}
