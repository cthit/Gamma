import axios from "axios";

import {
    RESET_PASSWORD_FINISH_SUCCESSFULLY,
    RESET_PASSWORD_FINISH_FAILED,
    RESET_PASSWORD_INITALIZE_SUCCESSFULLY,
    RESET_PASSWORD_INITALIZE_FAILED
} from "./ResetPassword.actions";
import { getBackendUrl } from "../../common/utils/configs/envVariablesLoader";

export function resetPasswordInitalize(cid) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            axios
                .post(getBackendUrl() + "/users/reset_password", {
                    cid: cid
                })
                .then(response => {
                    dispatch(resetPasswordInitalizeSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(resetPasswordInitalizeFailed(error));
                    reject(error);
                });
        });
    };
}

export function resetPasswordFinish(data) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            axios
                .put(getBackendUrl() + "/users/reset_password/finish", data)
                .then(response => {
                    dispatch(resetPasswordFinishSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(resetPasswordFinishFailed(error));
                    reject(error);
                });
        });
    };
}

function resetPasswordInitalizeSuccessfully() {
    return {
        type: RESET_PASSWORD_INITALIZE_SUCCESSFULLY,
        error: false
    };
}

function resetPasswordInitalizeFailed(error) {
    return {
        type: RESET_PASSWORD_INITALIZE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function resetPasswordFinishSuccessfully() {
    return {
        type: RESET_PASSWORD_FINISH_SUCCESSFULLY,
        error: false
    };
}

function resetPasswordFinishFailed(error) {
    return {
        type: RESET_PASSWORD_FINISH_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}
