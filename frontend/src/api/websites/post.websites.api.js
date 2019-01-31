import { postRequest } from "../utils/api";
import { ADMIN_WEBSITES_ENDPOINT } from "../utils/endpoints";

export function addWebsite(websiteData) {
    return postRequest(ADMIN_WEBSITES_ENDPOINT, websiteData);
}
