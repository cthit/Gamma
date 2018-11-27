import {
    GROUPS_LOAD_SUCCESSFULLY,
    GROUPS_LOAD_FAILED,
    GROUPS_ADD_SUCCESSFULLY,
    GROUPS_ADD_FAILED,
    GROUPS_DELETE_SUCCESSFULLY,
    GROUPS_DELETE_FAILED,
    GROUPS_CHANGE_SUCCESSFULLY,
    GROUPS_CHANGE_FAILED
} from "./actions.groups.api";

import { addGroup } from "./post.groups.api";
import { getGroups } from "./get.groups.api";
import { editGroup } from "./put.groups.api";
import { deleteGroup } from "./delete.groups.api";

export function createGetGroupsAction() {
    return dispatch => {
        return new Promise((resolve, reject) => {
            getGroups()
                .then(response => {
                    dispatch(groupsLoadSuccessfully(response.data));
                    resolve(response);
                })
                .catch(error => {
                    dispatch(groupsLoadFailed(error));
                    reject(error);
                });
        });
    };
}

export function createAddGroupAction(group) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            addGroup(group)
                .then(response => {
                    dispatch(groupsAddSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(groupsAddFailed(error));
                    reject(error);
                });
        });
    };
}

export function createEditGroupAction(group, groupId) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            editGroup(groupId, group)
                .then(response => {
                    dispatch(groupsChangeSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(groupsChangeFailed(error));
                    reject(error);
                });
        });
    };
}

export function createDeleteGroupAction(groupId) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            deleteGroup(groupId)
                .then(response => {
                    groupsDeleteSuccessfully();
                    resolve(response);
                })
                .catch(error => {
                    groupsDeleteFailed(error);
                    reject(error);
                });
        });
    };
}

export function groupsAddUser() {
    return dispatch => {
        return new Promise((resolve, reject) => {});
    };
}

export function groupsDeleteUser() {
    return dispatch => {
        return new Promise((resolve, reject) => {});
    };
}

export function groupsChangePost() {
    return dispatch => {
        return new Promise((resolve, reject) => {});
    };
}

function groupsAddSuccessfully() {
    return {
        type: GROUPS_ADD_SUCCESSFULLY,
        error: false
    };
}

function groupsAddFailed(error) {
    return {
        type: GROUPS_ADD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function groupsLoadSuccessfully(data) {
    return {
        type: GROUPS_LOAD_SUCCESSFULLY,
        error: false,
        payload: {
            data: data
        }
    };
}

function groupsLoadFailed(error) {
    return {
        type: GROUPS_LOAD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function groupsChangeSuccessfully() {
    return {
        type: GROUPS_CHANGE_SUCCESSFULLY,
        error: false
    };
}

function groupsChangeFailed(error) {
    return {
        type: GROUPS_CHANGE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function groupsDeleteSuccessfully() {
    return {
        type: GROUPS_DELETE_SUCCESSFULLY,
        error: false
    };
}

function groupsDeleteFailed(error) {
    return {
        type: GROUPS_DELETE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}
