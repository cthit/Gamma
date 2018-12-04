import { getActivationCodes } from "./get.activationCodes.api";
import { deleteActivationCode } from "./delete.activationCodes.api";

import {
    ACTIVATION_CODES_LOAD_SUCCESSFULLY,
    ACTIVATION_CODES_LOAD_FAILED,
    ACTIVATION_CODES_DELETE_FAILED,
    ACTIVATION_CODES_DELETE_SUCCESSFULLY
} from "./actions.activationCodes.api";

export function createGetActivationCodesAction() {
    return dispatch => {
        return new Promise((resolve, reject) => {
            getActivationCodes()
                .then(response => {
                    dispatch(activationCodesLoadSuccessfully(response.data));
                    resolve(response.data);
                })
                .catch(error => {
                    dispatch(activationCodesLoadFailed(error));
                    reject(error);
                });
        });
    };
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

function activationCodesLoadSuccessfully(data) {
    return {
        type: ACTIVATION_CODES_LOAD_SUCCESSFULLY,
        error: false,
        payload: {
            data: data
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
