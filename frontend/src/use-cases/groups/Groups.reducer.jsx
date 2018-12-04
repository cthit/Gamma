import { GROUPS_LOAD_SUCCESSFULLY } from "../../api/groups/actions.groups.api";

export function groups(state = [], action) {
    switch (action.type) {
        case GROUPS_LOAD_SUCCESSFULLY:
            return [...action.payload.data];
        default:
            return state;
    }
}
