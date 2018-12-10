import {
    GROUPS_GET_SUCCESSFULLY,
    GROUPS_LOAD_SUCCESSFULLY
} from "../../api/groups/actions.groups.api";

export function groups(state = [], action) {
    switch (action.type) {
        case GROUPS_GET_SUCCESSFULLY: //Should be func #256
            const group = { ...action.payload.data };
            group.func = group["function"];
            return [group];
        case GROUPS_LOAD_SUCCESSFULLY:
            return [...action.payload.data];
        default:
            return state;
    }
}
