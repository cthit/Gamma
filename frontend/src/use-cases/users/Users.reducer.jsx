import {
    USER_GET_SUCCESSFULLY,
    USERS_LOAD_MINIFIED_SUCCESSFULLY,
    USERS_LOAD_SUCCESSFULLY
} from "../../api/users/actions.users.api";

import { USER_LOGOUT_SUCCESSFULLY } from "../../app/elements/user-information/UserInformation.element.actions";

export function users(state = [], action) {
    switch (action.type) {
        case USERS_LOAD_MINIFIED_SUCCESSFULLY:
            return [...action.payload.data];
        case USER_GET_SUCCESSFULLY:
            return [action.payload.data];
        case USERS_LOAD_SUCCESSFULLY:
            return [...action.payload.data];
        case USER_LOGOUT_SUCCESSFULLY:
            return [];
        default:
            return state;
    }
}
