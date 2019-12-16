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
    USER_CHANGE_PASSWORD_FAILED
} from "./actions.users.api";

import { deleteUser } from "./delete.users.api";
import { editUser, editPassword } from "./put.users.api";
import { getUser, getUsers, getUsersMinified } from "./get.users.api";
import { requestPromise } from "../utils/requestPromise";
import { failed, loading, successfully } from "../utils/simpleActionCreators";
import { addUser } from "./post.users.api";

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

export function createEditUserPasswordAction(id, passwordData) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            editPassword(id, passwordData)
                .then(response => {
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

function usersChangePasswordSuccessfully(response) {
    return {
        type: USER_CHANGE_PASSWORD_SUCCESSFULLY,
        error: false
    };
}

function usersChangePasswordFailed(error) {
    return {
        type: USER_CHANGE_PASSWORD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}
