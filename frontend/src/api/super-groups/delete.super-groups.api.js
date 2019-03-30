import { deleteRequest } from "../utils/api";
import { ADMIN_SUPER_GROUPS_ENDPOINT } from "../utils/endpoints";

export function deleteSuperGroup(superGroupId) {
    return deleteRequest(ADMIN_SUPER_GROUPS_ENDPOINT + superGroupId);
}
