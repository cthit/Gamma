import {
    getActivationCode,
    getActivationCodes
} from "./get.activationCodes.api";
import { deleteActivationCode } from "./delete.activationCodes.api";

import {
    ACTIVATION_CODES_LOAD_SUCCESSFULLY,
    ACTIVATION_CODES_LOAD_FAILED,
    ACTIVATION_CODES_DELETE_FAILED,
    ACTIVATION_CODES_DELETE_SUCCESSFULLY,
    ACTIVATION_CODE_GET_LOADING,
    ACTIVATION_CODE_GET_SUCCESSFULLY,
    ACTIVATION_CODE_GET_FAILED,
    ACTIVATION_CODES_LOAD_LOADING
} from "./actions.activationCodes.api";
import { requestPromise } from "../utils/requestPromise";

export function createGetActivationCodesAction() {
    return requestPromise(
        getActivationCodes,
        activationCodesLoadLoading,
        activationCodesLoadSuccessfully,
        activationCodesLoadFailed
    );
}

export function createGetActivationCodeAction(activationCodeId) {
    return requestPromise(
        () => {
            return getActivationCode(activationCodeId);
        },
        activationCodeGetLoading,
        activationCodeGetSuccessfully,
        activationCodeGetFailed
    );
}

export function createDeleteActivationCodeAction(activationCodeId) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            deleteActivationCode(activationCodeId)
                .then(response => {
                    dispatch(activationCodesDeleteSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(activationCodesDeleteFailed());
                    reject(error);
                });
        });
    };
}

function activationCodesLoadLoading() {
    return {
        type: ACTIVATION_CODES_LOAD_LOADING,
        error: false
    };
}

function activationCodesLoadSuccessfully(response) {
    return {
        type: ACTIVATION_CODES_LOAD_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data
        }
    };
}

function activationCodesLoadFailed(error) {
    return {
        type: ACTIVATION_CODES_LOAD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function activationCodeGetLoading() {
    return {
        type: ACTIVATION_CODE_GET_LOADING,
        error: false
    };
}

function activationCodeGetSuccessfully(response) {
    return {
        type: ACTIVATION_CODE_GET_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data
        }
    };
}

function activationCodeGetFailed(error) {
    return {
        type: ACTIVATION_CODE_GET_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function activationCodesDeleteSuccessfully() {
    return {
        type: ACTIVATION_CODES_DELETE_SUCCESSFULLY,
        error: false
    };
}

function activationCodesDeleteFailed(error) {
    return {
        type: ACTIVATION_CODES_DELETE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}
