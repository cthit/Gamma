import {
    ME_CHANGE_FAILED,
    ME_CHANGE_SUCCESSFULLY,
    ME_GET_LOADING,
    ME_GET_FAILED,
    ME_GET_SUCCESSFULLY,
    ME_CHANGE_PASSWORD_FAILED,
    ME_CHANGE_PASSWORD_SUCCESSFULLY,
    ME_DELETED_FAILED,
    ME_DELETED_SUCCESSFULLY
} from "./actions.me.api";

import { editMe, editPassword } from "./put.me.api";
import { getMe } from "./get.me.api";

import { requestPromise } from "../utils/requestPromise";
import { failed, loading, successfully } from "../utils/simpleActionCreators";
import { deleteMe } from "./delete.me.api";

export function createGetMeAction() {
    return requestPromise(
        () => {
            return getMe();
        },
        meGetLoading,
        meGetSuccessfully,
        meGetFailed()
    );
}

export function createEditMeAction(userData) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            editMe(userData)
                .then(response => {
                    dispatch(meChangePasswordSuccessfully());
                    resolve(response.data);
                })
                .catch(error => {
                    dispatch(meChangeFailed(error));
                    reject(error);
                });
        });
    };
}

export function createChangeMePasswordAction(passwordData) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            editPassword(passwordData)
                .then(response => {
                    dispatch(meChangePasswordSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(meChangePasswordFailed());
                    reject(error);
                });
        });
    };
}

export function createDeleteMeAction(passwordData) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            deleteMe(passwordData)
                .then(response => {
                    dispatch(meDeleteSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(meDeleteFailed());
                    reject(error);
                });
        });
    };
}

function meChangeFailed(error) {
    return {
        type: ME_CHANGE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function meChangeSuccessfully() {
    return {
        type: ME_CHANGE_SUCCESSFULLY,
        error: false
    };
}

function meGetLoading() {
    return {
        type: ME_GET_LOADING,
        error: false
    };
}

function meGetSuccessfully(response) {
    return {
        type: ME_GET_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data
        }
    };
}

function meGetFailed(error) {
    return {
        type: ME_GET_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function meChangePasswordSuccessfully() {
    return {
        type: ME_CHANGE_PASSWORD_SUCCESSFULLY,
        error: false
    };
}

function meChangePasswordFailed(error) {
    return {
        type: ME_CHANGE_PASSWORD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function meDeleteSuccessfully() {
    return {
        type: ME_DELETED_SUCCESSFULLY,
        error: false
    };
}

function meDeleteFailed(error) {
    return {
        type: ME_DELETED_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}
