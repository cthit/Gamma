import { postRequest } from "../utils/api";
import { ADMIN_SUPER_GROUPS_ENDPOINT } from "../utils/endpoints";

export function addSuperGroup(superGroupData) {
    return postRequest(ADMIN_SUPER_GROUPS_ENDPOINT, superGroupData);
}
