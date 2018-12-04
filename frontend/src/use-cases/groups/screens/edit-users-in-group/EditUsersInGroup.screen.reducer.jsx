import { TEMPORARY_SAVE_SELECTED_USERS_TO_GROUP } from "./EditUsersInGroup.screen.actions";

export function editUsersInGroup(state = {}, action) {
    switch (action.type) {
        case TEMPORARY_SAVE_SELECTED_USERS_TO_GROUP:
            const newState = { ...state };

            newState[action.payload.groupId] = action.payload.selectedUsers;

            return newState;
        default:
            return state;
    }
}
