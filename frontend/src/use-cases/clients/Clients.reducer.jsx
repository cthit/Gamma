import {
    CLIENT_GET_SUCCESSFULLY,
    CLIENTS_LOAD_SUCCESSFULLY
} from "../../api/clients/actions.clients.api";

export function clients(state = [], action) {
    switch (action.type) {
        case CLIENT_GET_SUCCESSFULLY:
            return [action.payload.data];
        case CLIENTS_LOAD_SUCCESSFULLY:
            return [...action.payload.data];
        default:
            return state;
    }
}
