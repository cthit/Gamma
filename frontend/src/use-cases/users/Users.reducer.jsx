import {
    USER_GET_SUCCESSFULLY,
    USERS_LOAD_SUCCESSFULLY
} from "../../api/users/actions.users.api";

import { USER_LOGOUT_SUCCESSFULLY } from "../../app/elements/user-information/UserInformation.element.actions";

export function users(state = [], action) {
    switch (action.type) {
        case USER_GET_SUCCESSFULLY:
            return [action.payload.data];
        case USERS_LOAD_SUCCESSFULLY:
            console.log(action.payload);
            return [...action.payload.data];
        case USER_LOGOUT_SUCCESSFULLY:
            return [];
        default:
            return state;
    }
}
