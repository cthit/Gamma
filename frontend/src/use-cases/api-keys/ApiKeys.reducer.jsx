import {
    API_KEY_GET_SUCCESSFULLY,
    API_KEYS_LOAD_SUCCESSFULLY,
} from "../../api/api-keys/actions.api-keys.api";

export function apiKeys(state = [], action) {
    switch (action.type) {
        case API_KEY_GET_SUCCESSFULLY:
            return [action.payload.data];
        case API_KEYS_LOAD_SUCCESSFULLY:
            return [...action.payload.data];
        default:
            return state;
    }
}