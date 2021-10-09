import { putRequest } from "../utils/api";
import { ADMIN_SUPER_GROUPS_ENDPOINT } from "../utils/endpoints";

export function editSuperGroup(superGroupId, newSuperGroupData) {
    return putRequest(ADMIN_SUPER_GROUPS_ENDPOINT + superGroupId, {
        ...newSuperGroupData,
        type: newSuperGroupData.type.toLowerCase()
    });
}
