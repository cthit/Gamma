import {
    USERS_CHANGE_FAILED,
    USERS_CHANGE_SUCCESSFULLY,
    USERS_DELETE_FAILED,
    USERS_DELETE_SUCCESSFULLY,
    USERS_LOAD_FAILED,
    USERS_LOAD_SUCCESSFULLY
} from "./actions.users.api";

import { deleteUser } from "./delete.users.api";
import { editUser } from "./put.users.api";
import { getUsers } from "./get.users.api";

export function createGetUsersAction() {
    return dispatch => {
        return new Promise((resolve, reject) => {
            getUsers()
                .then(response => {
                    dispatch(usersLoadSuccessfully(response.data));
                    resolve(response.data);
                })
                .catch(error => {
                    dispatch(usersLoadFailed(error));
                    reject(error);
                });
        });
    };
}

export function createEditUserAction(user, cid) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            editUser(cid, user)
                .then(response => {
                    dispatch(usersChangeSuccessfully());
                    resolve(response.data);
                })
                .catch(error => {
                    dispatch(usersChangeFailed(error));
                    reject(error);
                });
        });
    };
}

export function createDeleteUserAction(cid) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            deleteUser(cid)
                .then(response => {
                    dispatch(usersDeleteSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(usersDeleteFailed(error));
                    reject(error);
                });
        });
    };
}

function usersLoadFailed(error) {
    return {
        type: USERS_LOAD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function usersLoadSuccessfully(data) {
    return {
        type: USERS_LOAD_SUCCESSFULLY,
        error: false,
        payload: {
            ...data
        }
    };
}

function usersChangeSuccessfully() {
    return {
        type: USERS_CHANGE_SUCCESSFULLY,
        error: false
    };
}

function usersChangeFailed(error) {
    return {
        type: USERS_CHANGE_FAILED,
        error: false,
        payload: {
            error: error
        }
    };
}

function usersDeleteSuccessfully() {
    return {
        type: USERS_DELETE_SUCCESSFULLY,
        error: false
    };
}

function usersDeleteFailed(error) {
    return {
        type: USERS_DELETE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}
