import { getRequest } from "../utils/api";
import { SUPER_GROUPS_ENDPOINT } from "../utils/endpoints";

export function getSuperGroups() {
    return getRequest(SUPER_GROUPS_ENDPOINT);
}

export function getSuperGroup(id) {
    return getRequest(SUPER_GROUPS_ENDPOINT + id);
}

export function getSuperGroupSubGroups(id) {
    return getRequest(
        SUPER_GROUPS_ENDPOINT + id + "/subgroups",
        true,
        input => ({ data: { subGroups: input.data } })
    );
}
