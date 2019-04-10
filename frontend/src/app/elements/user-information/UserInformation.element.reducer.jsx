import {
    USER_UPDATE_ME,
    USER_UPDATED_SUCCESSFULLY,
    USER_UPDATED_FAILED,
    USER_NOT_LOGGED_IN,
    USER_LOGOUT_SUCCESSFULLY
} from "./UserInformation.element.actions";

export function user(state = { loaded: false }, action) {
    switch (action.type) {
        case USER_UPDATE_ME:
            return {
                errorLoadingUser: false,
                loaded: false
            };
        case USER_UPDATED_SUCCESSFULLY:
            return {
                loggedIn: true,
                loaded: true,
                errorLoadingUser: false,
                ...action.payload.user
            };
        case USER_UPDATED_FAILED:
            return {
                loggedIn: false,
                loaded: true,
                errorLoadingUser: true
            };
        case USER_NOT_LOGGED_IN:
            return {
                loggedIn: false,
                errorLoadingUser: false,
                loaded: true
            };
        case USER_LOGOUT_SUCCESSFULLY:
            return {
                loggedIn: false,
                errorLoadingUser: false,
                loaded: true
            };
        default:
            return state;
    }
}
