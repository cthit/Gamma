import { postRequest } from "../utils/api";
import { USERS_ENDPOINT } from "../utils/endpoints";

/**
 * {
 *      TODO
 * }
 */
export function addUser(userData) {
  return postRequest(USERS_ENDPOINT, userData);
}
