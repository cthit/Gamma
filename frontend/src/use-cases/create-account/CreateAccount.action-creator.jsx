import {
    CREATE_ACCOUNT_VALIDATING_CID,
    CREATE_ACCOUNT_VALIDATE_CID_FAILED,
    CREATE_ACCOUNT_VALIDATE_CID_SUCCESSFULLY,
    CREATE_ACCOUNT_VALIDATING_CODE_AND_DATA,
    CREATE_ACCOUNT_VALIDATE_CODE_FAILED,
    CREATE_ACCOUNT_VALIDATE_DATA_FAILED,
    CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA_SUCCESSFULLY,
    CREATE_ACCOUNT_COMPLETED
} from "./CreateAccount.actions";

import {
    DigitToastActions,
    DigitRedirectActions
} from "@cthit/react-digit-components";

import { activateCid } from "../../api/whitelist/post.whitelist.api";
import { createAccount } from "../../api/create-account/post.createAccount.api";

export function createAccountValidateCid(data, errorMsg) {
    return dispatch => {
        dispatch(createAccountValidatingCid());
        return new Promise((resolve, reject) => {
            activateCid(data)
                .then(response => {
                    dispatch(createAccountValidateCidSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(createAccountValidateCidFailed(errorMsg));
                    reject(error);
                });
        });
    };
}

export function createAccountValidatingCid() {
    return {
        type: CREATE_ACCOUNT_VALIDATING_CID,
        error: false
    };
}

export function createAccountValidateCidFailed(error) {
    return {
        type: CREATE_ACCOUNT_VALIDATE_CID_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

export function createAccountValidateCidSuccessfully() {
    return {
        type: CREATE_ACCOUNT_VALIDATE_CID_SUCCESSFULLY,
        error: false
    };
}

//data includes code. Ignore code for now
export function createAccountValidateCodeAndData(data) {
    return dispatch => {
        dispatch(createAccountValidatingCodeAndData());
        return new Promise((resolve, reject) => {
            createAccount(data)
                .then(response => {
                    dispatch(createAccountValidateCodeAndDataSuccessfully());

                    resolve(response);
                })
                .catch(error => {
                    dispatch(createAccountValidateDataFailed(error));
                    reject(error);
                });
        });
    };
}

export function createAccountValidatingCodeAndData() {
    return {
        type: CREATE_ACCOUNT_VALIDATING_CODE_AND_DATA,
        error: false
    };
}

export function createAccountValidateCodeFailed(error) {
    return {
        type: CREATE_ACCOUNT_VALIDATE_CODE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

export function createAccountValidateDataFailed(error) {
    return {
        type: CREATE_ACCOUNT_VALIDATE_DATA_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

export function createAccountValidateCodeAndDataSuccessfully() {
    return {
        type: CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA_SUCCESSFULLY,
        error: false
    };
}

export function createAccountCompleted() {
    return {
        type: CREATE_ACCOUNT_COMPLETED,
        error: false
    };
}
