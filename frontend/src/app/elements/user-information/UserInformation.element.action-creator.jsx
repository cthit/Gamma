import { DigitRedirectActions } from "@cthit/react-digit-components";
import axios from "axios";
import token from "../../../common/utils/retrievers/token.retrieve";
import {
    USER_LOGOUT_SUCCESSFULLY,
    USER_NOT_LOGGED_IN,
    USER_UPDATED_FAILED,
    USER_UPDATED_SUCCESSFULLY
} from "./UserInformation.element.actions";

export function userUpdateMe() {
    if (token() == null) {
        return {
            type: USER_NOT_LOGGED_IN,
            error: false
        };
    } else {
        return dispatch => {
            return new Promise((resolve, reject) => {
                axios
                    .get(
                        (process.env.REACT_APP_BACKEND_URL ||
                            "http://localhost:8081") + "/api/users/me",
                        {
                            headers: {
                                Authorization: "Bearer " + token()
                            }
                        }
                    )
                    .then(response => {
                        resolve();
                        dispatch(userUpdatedSuccessfully(response.data));
                    })
                    .catch(error => {
                        reject();
                        dispatch(userUpdatedFailed(error));
                        const statusCode =
                            error.response == null
                                ? -1
                                : error.response.data.status;
                        switch (statusCode) {
                            case 403:
                                dispatch(userLogout());
                                break;
                            default:
                                //TODO DISPATCH REDIRECT TO ERROR PAGE
                                break;
                        }
                    });
            });
        };
    }
}

export function userLogout() {
    return dispatch => {
        delete localStorage.token;
        delete sessionStorage.token;
        dispatch(
            DigitRedirectActions.digitRedirectTo(
                (process.env.REACT_APP_BACKEND_URL || "http://localhost:8081") +
                    "/api/logout",
                true
            )
        );
    };
}

export function userLogoutSuccessfully() {
    return {
        type: USER_LOGOUT_SUCCESSFULLY,
        error: false
    };
}

export function userUpdatedSuccessfully(data) {
    return {
        type: USER_UPDATED_SUCCESSFULLY,
        payload: {
            user: data
        },
        error: false
    };
}

export function userUpdatedFailed(errorData) {
    return {
        type: USER_UPDATED_FAILED,
        error: true,
        payload: {
            error: errorData
        }
    };
}
