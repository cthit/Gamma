import { getRequest } from "../utils/api";
import { ADMIN_WEBSITES_ENDPOINT } from "../utils/endpoints";

export function getWebsites() {
    return getRequest(ADMIN_WEBSITES_ENDPOINT);
}

export function getWebsite(websiteId) {
    return getRequest(ADMIN_WEBSITES_ENDPOINT + websiteId);
}
