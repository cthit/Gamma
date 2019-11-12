import { getRequest } from "../utils/api";
import { WEBSITES_ENDPOINT } from "../utils/endpoints";

export function getWebsites() {
    return getRequest(WEBSITES_ENDPOINT);
}

export function getWebsite(websiteId) {
    return getRequest(WEBSITES_ENDPOINT + websiteId);
}
