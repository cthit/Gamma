import { TEMPORARY_SAVE_SELECTED_USERS_TO_GROUP } from "./EditUsersInGroup.screen.actions";

export function temporarySaveSelectedUsersToGroup(groupId, selectedUsers) {
    return {
        type: TEMPORARY_SAVE_SELECTED_USERS_TO_GROUP,
        error: false,
        payload: {
            groupId: groupId,
            selectedUsers: selectedUsers
        }
    };
}
