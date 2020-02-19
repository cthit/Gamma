import { getRequest } from "../utils/api";
import { ADMIN_GROUPS_ENDPOINT, GROUPS_ENDPOINT } from "../utils/endpoints";

const MINIFIED = "minified/";

export function getGroups() {
    return getRequest(ADMIN_GROUPS_ENDPOINT);
}

export function getGroup(groupId) {
    return getRequest(GROUPS_ENDPOINT + groupId, true, input => ({
        data: {
            ...input.data,
            functionSv: input.data["function"].sv,
            functionEn: input.data["function"].en,
            descriptionSv: input.data.description.sv,
            descriptionEn: input.data.description.en,
            superGroup: input.data.superGroup.id
        }
    }));
}

export function getGroupsMinified() {
    return getRequest(GROUPS_ENDPOINT + MINIFIED, true, input =>
        input.data.map(one => ({
            functionSv: one.function.sv,
            functionEn: one.function.en,
            descriptionEn: one.description.en,
            descriptionSv: one.description.sv,
            ...one
        }))
    );
}

export function getGroupMinified(groupId) {
    return getRequest(GROUPS_ENDPOINT + groupId + "/" + MINIFIED);
}
