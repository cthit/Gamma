import { postRequest } from "../utils/api";
import { LOGIN_ENDPOINT } from "../utils/endpoints";

export function login(data) {
  return postRequest(LOGIN_ENDPOINT, data);
}
