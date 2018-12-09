import { USER_LOGOUT_SUCCESSFULLY } from "../../app/elements/user-information/UserInformation.element.actions";
import {
    WHITELIST_ITEM_LOAD_SUCCESSFULLY,
    WHITELIST_LOAD_SUCCESSFULLY
} from "../../api/whitelist/actions.whitelist.api";

export function whitelist(state = [], action) {
    switch (action.type) {
        case WHITELIST_ITEM_LOAD_SUCCESSFULLY:
            console.log(action.payload);
            return [...state, ...action.payload.data];
        case WHITELIST_LOAD_SUCCESSFULLY:
            return [...action.payload.data];
        case USER_LOGOUT_SUCCESSFULLY:
            return [];
        default:
            return state;
    }
}
