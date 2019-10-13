import { getRequest } from "../utils/api";
import { ADMIN_GROUPS_ENDPOINT, GROUPS_ENDPOINT } from "../utils/endpoints";

const MINIFIED = "minified/";

export function getGroups() {
    return getRequest(ADMIN_GROUPS_ENDPOINT);
}

export function getGroup(groupId) {
    return getRequest(GROUPS_ENDPOINT + groupId, true, input => ({
        data: {
            id: input.data.id,
            name: input.data.name,
            functionSv: input.data["function"].sv,
            functionEn: input.data["function"].en,
            descriptionSv: input.data.description.sv,
            descriptionEn: input.data.description.en,
            email: input.data.email
        }
    }));
}

export function getGroupsMinified() {
    return getRequest(GROUPS_ENDPOINT + MINIFIED, true, input =>
        input.data.map(one => ({
            id: one.id,
            name: one.name,
            email: one.email
        }))
    );
}

export function getGroupMinified(groupId) {
    return getRequest(GROUPS_ENDPOINT + groupId + "/" + MINIFIED);
}
