import { USERS_LOAD_SUCCESSFULLY } from "./Users.actions";

import { USER_LOGOUT_SUCCESSFULLY } from "../../app/elements/user-information/UserInformation.element.actions";

export function users(state = [], action) {
    switch (action.type) {
        case USERS_LOAD_SUCCESSFULLY:
            return Object.keys(action.payload).map(
                index => action.payload[index]
            );
        case USER_LOGOUT_SUCCESSFULLY:
            return [];
        default:
            return state;
    }
}
