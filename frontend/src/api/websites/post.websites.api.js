import { postRequest } from "../utils/api";
import { WEBSITES_ENDPOINT } from "../utils/endpoints";

export function addWebsite(websiteData) {
  return postRequest(WEBSITES_ENDPOINT, websiteData);
}
