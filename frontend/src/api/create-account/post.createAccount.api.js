import { postRequest } from "../utils/api";
import { CREATE_ACCOUNT_ENDPOINT } from "../utils/endpoints";

const CREATE = "create/";

export function createAccount(data) {
    return postRequest(CREATE_ACCOUNT_ENDPOINT + CREATE, data, false);
}
