import { getRequest } from "../utils/api";
import { ADMIN_GROUPS_ENDPOINT, GROUPS_ENDPOINT } from "../utils/endpoints";

const MINIFIED = "minified/";

export function getGroups() {
    return getRequest(ADMIN_GROUPS_ENDPOINT);
}

export function getGroup(groupId) {
    return getRequest(GROUPS_ENDPOINT + groupId);
}

// export function getGroupsMinified() {
//     return getRequest();
// }

export function getGroupMinified(groupId) {
    return getRequest(ADMIN_GROUPS_ENDPOINT + groupId + "/" + MINIFIED);
}
