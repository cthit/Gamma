import {
    WEBSITE_GET_SUCCESSFULLY,
    WEBSITES_LOAD_SUCCESSFULLY
} from "../../api/websites/actions.websites.api";

export function websites(state = [], action) {
    switch (action.type) {
        case WEBSITES_LOAD_SUCCESSFULLY:
            return [...action.payload.websites];
        case WEBSITE_GET_SUCCESSFULLY:
            return [action.payload.data];
        default:
            return state;
    }
}
