import { getRequest } from "../utils/api";
import { USERS_ENDPOINT } from "../utils/endpoints";

export function getUsers() {
  return getRequest(USERS_ENDPOINT);
}

export function getUser(userId) {
  return getRequest(USERS_ENDPOINT + userId);
}
