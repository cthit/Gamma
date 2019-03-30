import {
    SUPER_GROUP_GET_SUB_GROUPS_SUCCESSFULLY,
    SUPER_GROUP_GET_SUCCESSFULLY,
    SUPER_GROUPS_LOAD_SUCCESSFULLY
} from "../../api/super-groups/actions.super-groups.api";

export function superGroups(state = {}, action) {
    switch (action.type) {
        case SUPER_GROUPS_LOAD_SUCCESSFULLY:
            return { all: [...action.payload.data] };
        case SUPER_GROUP_GET_SUCCESSFULLY:
            return {
                details: {
                    ...state.details,
                    data: { ...action.payload.data }
                }
            };
        case SUPER_GROUP_GET_SUB_GROUPS_SUCCESSFULLY:
            return {
                details: {
                    ...state.details,
                    subGroups: [...action.payload.data]
                }
            };
        default:
            return state;
    }
}
