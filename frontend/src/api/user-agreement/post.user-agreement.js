import { ADMIN_USER_AGREEMENT_ENDPOINT } from "../utils/endpoints";
import { postRequest } from "../utils/api";

export const resetUserAgreement = data =>
    postRequest(ADMIN_USER_AGREEMENT_ENDPOINT, data);
