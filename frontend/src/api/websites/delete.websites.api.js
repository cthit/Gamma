import { deleteRequest } from "../utils/api";
import { ADMIN_WEBSITES_ENDPOINT } from "../utils/endpoints";

export function deleteWebsite(websiteId) {
    return deleteRequest(ADMIN_WEBSITES_ENDPOINT + websiteId);
}
