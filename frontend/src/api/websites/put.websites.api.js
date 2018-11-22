import { putRequest } from "../utils/api";
import { WEBSITES_ENDPOINT } from "../utils/endpoints";

export function editWebsite(websiteId, newWebsiteData) {
  console.log(WEBSITES_ENDPOINT + websiteId);
  return putRequest(WEBSITES_ENDPOINT + websiteId, newWebsiteData);
}
