import { putRequest } from "../utils/api";
import { ADMIN_WEBSITES_ENDPOINT } from "../utils/endpoints";

export function editWebsite(websiteId, newWebsiteData) {
    return putRequest(ADMIN_WEBSITES_ENDPOINT + websiteId, newWebsiteData);
}
