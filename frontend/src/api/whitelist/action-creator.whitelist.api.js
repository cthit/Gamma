import { deleteWhitelistItem } from "./delete.whitelist.api";
import { getWhitelist, getWhitelistItem } from "./get.whitelist.api";
import { addUsersToWhitelist, cidIsWhitelisted } from "./post.whitelist.api";
import { editWhitelistItem } from "./put.whitelist.api";
import {
    WHITELIST_ADD_SUCCESSFULLY,
    WHITELIST_CHANGE_FAILED,
    WHITELIST_CHANGE_SUCCESSFULLY,
    WHITELIST_DELETE_FAILED,
    WHITELIST_DELETE_SUCCESSFULLY,
    WHITELIST_LOADING,
    WHITELIST_LOAD_FAILED,
    WHITELIST_LOAD_SUCCESSFULLY,
    WHITELIST_VALIDATE_FAILED,
    WHITELIST_VALIDATE_SUCCESSFULLY,
    WHITELIST_ADD_FAILED,
    WHITELIST_ITEM_LOAD_FAILED,
    WHITELIST_ITEM_LOAD_SUCCESSFULLY,
    WHITELIST_ITEM_LOAD_LOADING
} from "./actions.whitelist.api";

import { requestPromise } from "../utils/requestPromise";

export function createGetWhitelistItemAction(whitelistItemId) {
    return requestPromise(
        () => {
            return getWhitelistItem(whitelistItemId);
        },
        whitelistItemLoading,
        whitelistItemLoadSuccessfully,
        whitelistItemLoadFailed
    );
}

export function createGetWhitelistAction() {
    return dispatch => {
        dispatch(whitelistLoading());
        return new Promise((resolve, reject) => {
            getWhitelist()
                .then(response => {
                    dispatch(whitelistLoadSuccessfully(response.data));
                    resolve(response);
                })
                .catch(error => {
                    dispatch(whitelistLoadFailed(error));
                    resolve(reject);
                });
        });
    };
}

export function createAddToWhitelistAction(whitelist) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            addUsersToWhitelist(whitelist)
                .then(response => {
                    dispatch(whitelistAddSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(whitelistAddFailed(error));
                    reject(error);
                });
        });
    };
}

export function createDeleteWhitelistItemAction(whitelistId) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            deleteWhitelistItem(whitelistId)
                .then(response => {
                    dispatch(whitelistDeleteSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(whitelistDeleteFailed(error));
                    reject(error);
                });
        });
    };
}

export function createEditWhitelistItemAction(whitelist, whitelistId) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            editWhitelistItem(whitelistId, whitelist)
                .then(response => {
                    dispatch(whitelistChangeSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(whitelistChangeFailed());
                    reject(error);
                });
        });
    };
}

export function createValidateWhitelistAction(cid) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            cidIsWhitelisted({ cid: cid })
                .then(response => {
                    dispatch(whitelistValidateSuccessfully(response.data));
                    resolve(response.data);
                })
                .catch(error => {
                    dispatch(whitelistValidateFailed(error));
                    reject(error);
                });
        });
    };
}

function whitelistItemLoading() {
    return {
        type: WHITELIST_ITEM_LOAD_LOADING,
        error: false
    };
}

function whitelistItemLoadSuccessfully(response) {
    return {
        type: WHITELIST_ITEM_LOAD_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data
        }
    };
}

function whitelistItemLoadFailed(error) {
    return {
        type: WHITELIST_ITEM_LOAD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function whitelistLoading() {
    return {
        type: WHITELIST_LOADING,
        error: false
    };
}

function whitelistLoadSuccessfully(data) {
    return {
        type: WHITELIST_LOAD_SUCCESSFULLY,
        error: false,
        payload: {
            data: [...data]
        }
    };
}

function whitelistLoadFailed(error) {
    return {
        type: WHITELIST_LOAD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function whitelistAddSuccessfully() {
    return {
        type: WHITELIST_ADD_SUCCESSFULLY,
        error: false
    };
}

function whitelistAddFailed(error) {
    return {
        type: WHITELIST_ADD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function whitelistDeleteSuccessfully() {
    return {
        type: WHITELIST_DELETE_SUCCESSFULLY,
        error: false
    };
}

function whitelistDeleteFailed(error) {
    return {
        type: WHITELIST_DELETE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function whitelistChangeSuccessfully() {
    return {
        type: WHITELIST_CHANGE_SUCCESSFULLY,
        error: false
    };
}

function whitelistChangeFailed(error) {
    return {
        type: WHITELIST_CHANGE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function whitelistValidateSuccessfully(valid) {
    return {
        type: WHITELIST_VALIDATE_SUCCESSFULLY,
        error: false,
        payload: {
            valid: valid
        }
    };
}

function whitelistValidateFailed(error) {
    return {
        type: WHITELIST_VALIDATE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}
