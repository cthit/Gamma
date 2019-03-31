import {
    GROUPS_GET_MINIFIED_SUCCESSFULLY,
    GROUP_GET_SUCCESSFULLY,
    GROUPS_LOAD_SUCCESSFULLY
} from "../../api/groups/actions.groups.api";

export function groups(state = {}, action) {
    switch (action.type) {
        case GROUP_GET_SUCCESSFULLY:
            return {
                details: action.payload.data
            };
        case GROUPS_GET_MINIFIED_SUCCESSFULLY:
            return {
                all: action.payload.data
            };
        case GROUPS_LOAD_SUCCESSFULLY:
            return {
                all: action.payload.data
            };
        default:
            return state;
    }
}
