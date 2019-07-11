import {
    USER_GET_FAILED,
    USER_GET_LOADING,
    USER_GET_SUCCESSFULLY,
    USERS_ADD_FAILED,
    USERS_ADD_LOADING,
    USERS_ADD_SUCCESSFULLY,
    USERS_CHANGE_FAILED,
    USERS_CHANGE_SUCCESSFULLY,
    USERS_DELETE_FAILED,
    USERS_DELETE_SUCCESSFULLY,
    USERS_LOAD_FAILED,
    USERS_LOAD_MINIFIED_FAILED,
    USERS_LOAD_MINIFIED_LOADING,
    USERS_LOAD_MINIFIED_SUCCESSFULLY,
    USERS_LOAD_SUCCESSFULLY,
    USER_CHANGE_PASSWORD_SUCCESSFULLY,
    USER_CHANGE_PASSWORD_FAILED,
} from "./actions.users.api";

import { deleteUser } from "./delete.users.api";
import { editUser, editPassword } from "./put.users.api";
import { getUser, getUsers, getUsersMinified } from "./get.users.api";
import { requestPromise } from "../utils/requestPromise";
import { failed, loading, successfully } from "../utils/simpleActionCreators";
import { addUser } from "./post.users.api";

export function createGetUsersMinifiedAction() {
    return requestPromise(
        getUsersMinified,
        usersGetMinifiedLoading,
        usersGetMinifiedSuccessfully,
        usersGetMinifiedFailed,
    );
}

export function createGetUsersAction() {
    return dispatch => {
        return new Promise((resolve, reject) => {
            getUsers()
                .then(response => {
                    dispatch(usersLoadSuccessfully(response));
                    resolve(response.data);
                })
                .catch(error => {
                    dispatch(usersLoadFailed(error));
                    reject(error);
                });
        });
    };
}

export function createGetUserAction(id) {
    return requestPromise(
        () => {
            return getUser(id);
        },
        userGetLoading,
        userGetSuccessfully,
        userGetFailed
    );
}

export function createEditUserAction(user, id) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            editUser(id, user)
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

export function createDeleteUserAction(id) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            deleteUser(id)
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

export function createAddUserAction(userData) {
    return requestPromise(
        () => {
            return addUser(userData);
        },
        addUserLoading,
        addUserSuccessfully,
        addUserFailed
    );
}

export function createEditUserPasswordAction(id, passwordData) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            editPassword(id, passwordData)
                .then( response => {
                    dispatch(usersChangePasswordSuccessfully());
                    resolve(response);
            })
                .catch(error => {
                    dispatch(usersChangePasswordFailed());
                    reject(error);
                });
        });
    };
}

function addUserLoading() {
    return loading(USERS_ADD_LOADING);
}

function addUserSuccessfully(response) {
    return successfully(USERS_ADD_SUCCESSFULLY, response);
}

function addUserFailed(error) {
    return failed(USERS_ADD_FAILED, error);
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

function usersLoadSuccessfully(response) {
    return {
        type: USERS_LOAD_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data
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

function userGetLoading() {
    return {
        type: USER_GET_LOADING,
        error: false
    };
}

function userGetSuccessfully(response) {
    return {
        type: USER_GET_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data
        }
    };
}

function userGetFailed(error) {
    return {
        type: USER_GET_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function usersGetMinifiedLoading() {
    return {
        type: USERS_LOAD_MINIFIED_LOADING,
        error: false
    };
}

function usersGetMinifiedSuccessfully(response) {
    return {
        type: USERS_LOAD_MINIFIED_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data
        }
    };
}

function usersGetMinifiedFailed(error) {
    return {
        type: USERS_LOAD_MINIFIED_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function usersChangePasswordSuccessfully(response) {
    return {
        type: USER_CHANGE_PASSWORD_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data,
        },
    }
}

function usersChangePasswordFailed(error) {
    return {
        type: USER_CHANGE_PASSWORD_FAILED,
        error: true,
        payload: {
            error: error,
        },
    }
}
