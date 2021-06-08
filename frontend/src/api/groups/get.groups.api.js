import { getRequest } from "../utils/api";
import { ADMIN_GROUPS_ENDPOINT, GROUPS_ENDPOINT } from "../utils/endpoints";

export function getGroups() {
    return getRequest(ADMIN_GROUPS_ENDPOINT);
}

export function getGroup(groupId) {
    return getRequest(GROUPS_ENDPOINT + groupId, input => ({
        data: {
            ...input.data,
            superGroup: input.data.superGroup.id,
            superGroupPrettyName: input.data.superGroup.prettyName,
            superGroupName: input.data.superGroup.name
        }
    }));
}

export function getGroupsMinified() {
    return getRequest(GROUPS_ENDPOINT);
}

export function getGroupMinified(groupId) {
    return getRequest(GROUPS_ENDPOINT + groupId);
}
