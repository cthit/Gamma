import {
    GROUPS_ADD_USER_SUCCESSFULLY,
    GROUPS_ADD_USER_FAILED,
    GROUPS_ADD_USER_LOADING,
    GROUPS_REMOVE_USER_FAILED,
    GROUPS_REMOVE_USER_LOADING,
    GROUPS_EDIT_USER_SUCCESSFULLY,
    GROUPS_EDIT_USER_FAILED,
    GROUPS_EDIT_USER_LOADING,
    GROUPS_REMOVE_USER_SUCCESSFULLY
} from "./actions.groups.api";

import { addUserToGroup } from "./post.groups.api";
import { editUserInGroup } from "./put.groups.api";
import { removeUserFromGroup } from "./delete.groups.api";
import { requestPromise } from "../utils/requestPromise";
import { failed, loading, successfully } from "../utils/simpleActionCreators";

export function createAddUserToGroupRequest(groupId, memberData) {
    return requestPromise(
        () => {
            return addUserToGroup(groupId, memberData);
        },
        addUserToGroupLoading,
        addUserToGroupSuccessfully,
        addUserToGroupFailed
    );
}

export function createDeleteUserFromGroupRequest(groupId, userId) {
    return requestPromise(
        () => {
            return removeUserFromGroup(groupId, userId);
        },
        removeUserFromGroupLoading,
        removeUserFromGroupSuccessfully,
        removeUserFromGroupFailed
    );
}

export function createEditUserInGroupRequest(groupId, userId, memberData) {
    return requestPromise(
        () => {
            return editUserInGroup(groupId, userId, memberData);
        },
        editUserInGroupLoading,
        editUserInGroupSuccessfully,
        editUserInGroupFailed
    );
}

function editUserInGroupSuccessfully(response) {
    return successfully(GROUPS_EDIT_USER_SUCCESSFULLY, response);
}

function editUserInGroupFailed(error) {
    return failed(GROUPS_EDIT_USER_FAILED, error);
}

function editUserInGroupLoading() {
    return loading(GROUPS_EDIT_USER_LOADING);
}

function removeUserFromGroupSuccessfully(response) {
    return successfully(GROUPS_REMOVE_USER_SUCCESSFULLY, response);
}

function removeUserFromGroupFailed(error) {
    return failed(GROUPS_REMOVE_USER_FAILED, error);
}

function removeUserFromGroupLoading() {
    return loading(GROUPS_REMOVE_USER_LOADING);
}

function addUserToGroupSuccessfully(response) {
    return successfully(GROUPS_ADD_USER_SUCCESSFULLY, response);
}

function addUserToGroupFailed(error) {
    return failed(GROUPS_ADD_USER_FAILED, error);
}

function addUserToGroupLoading() {
    return loading(GROUPS_ADD_USER_LOADING);
}
