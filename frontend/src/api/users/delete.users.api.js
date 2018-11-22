import { deleteRequest } from "../utils/api";
import { USERS_ENDPOINT } from "../utils/endpoints";

export function deleteUser(userId) {
  return deleteRequest(USERS_ENDPOINT + userId);
}
