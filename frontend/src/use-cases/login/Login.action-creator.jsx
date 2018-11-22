import { login as loginRequest } from "../../api/login/post.login.api";
import { userUpdateMe } from "../../app/elements/user-information/UserInformation.element.action-creator";
import {
    LOGIN_VALIDATE_FAILED,
    LOGIN_VALIDATE_SUCCESSFULLY,
    LOGIN_VALIDATING
} from "./Login.actions";

export function login(data, persistant) {
    return dispatch => {
        dispatch(loginValidating());
        return new Promise((resolve, reject) => {
            loginRequest(data)
                .then(response => {
                    const token = response.data;
                    if (persistant) {
                        localStorage.token = token;
                        delete sessionStorage.token;
                    } else {
                        sessionStorage.token = token;
                        delete localStorage.token;
                    }
                    dispatch(loginValidateSuccessfully());
                    dispatch(userUpdateMe());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(loginValidateFailed(error));
                    reject(error);
                });
        });
    };
}

export function loginValidating() {
    return {
        type: LOGIN_VALIDATING,
        error: false
    };
}

export function loginValidateFailed(error) {
    return {
        type: LOGIN_VALIDATE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

export function loginValidateSuccessfully() {
    return {
        type: LOGIN_VALIDATE_SUCCESSFULLY,
        error: false
    };
}
