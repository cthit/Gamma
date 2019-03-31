import { TEMPORARY_SAVE_SELECTED_USERS_TO_GROUP } from "./views/select-members/SelectMembers.view.actions";

export function editUsersInGroup(state = {}, action) {
    switch (action.type) {
        case TEMPORARY_SAVE_SELECTED_USERS_TO_GROUP:
            const newState = { ...state };

            newState[action.payload.groupId] = action.payload.selectedMemberIds;

            return newState;
        default:
            return state;
    }
}
