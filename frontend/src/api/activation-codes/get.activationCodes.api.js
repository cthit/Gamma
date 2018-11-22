import { getRequest } from "../utils/api";
import { ACTIVATION_CODES_ENDPOINT } from "../utils/endpoints";

export function getActivationCodes() {
  return getRequest(ACTIVATION_CODES_ENDPOINT);
}
