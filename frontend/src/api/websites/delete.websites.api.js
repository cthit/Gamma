import { deleteRequest } from "../utils/api";
import { WEBSITES_ENDPOINT } from "../utils/endpoints";

export function deleteWebsite(websiteId) {
    return deleteRequest(WEBSITES_ENDPOINT + websiteId);
}
