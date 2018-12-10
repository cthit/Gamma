import {
    USER_GET_FAILED,
    USER_GET_LOADING,
    USER_GET_SUCCESSFULLY,
    USERS_CHANGE_FAILED,
    USERS_CHANGE_SUCCESSFULLY,
    USERS_DELETE_FAILED,
    USERS_DELETE_SUCCESSFULLY,
    USERS_LOAD_FAILED, USERS_LOAD_MINIFIED_FAILED, USERS_LOAD_MINIFIED_LOADING, USERS_LOAD_MINIFIED_SUCCESSFULLY,
    USERS_LOAD_SUCCESSFULLY
} from "./actions.users.api";

import { deleteUser } from "./delete.users.api";
import { editUser } from "./put.users.api";
import { getUser, getUsers, getUsersMinified } from "./get.users.api";
import { requestPromise } from "../utils/requestPromise";

export function createGetUsersMinifiedAction() {
    return requestPromise(
        getUsersMinified,
        usersGetMinifiedLoading,
        usersGetMinifiedSuccessfully,
        usersGetMinifiedFailed
    )
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

export function createGetUserAction(cid) {
    return requestPromise(
        () => {
            return getUser(cid);
        },
        userGetLoading,
        userGetSuccessfully,
        userGetFailed
    );
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

function usersGetMinifiedLoading(){
    return {
        type: USERS_LOAD_MINIFIED_LOADING,
        error: false
    };
}

function usersGetMinifiedSuccessfully(response){
    return {
        type: USERS_LOAD_MINIFIED_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data
        }
    }
}

function usersGetMinifiedFailed(error){
    return {
        type: USERS_LOAD_MINIFIED_FAILED,
        error: true,
        payload: {
            error: error
        }
    }
}
