import {
    ACTIVATION_CODE_GET_SUCCESSFULLY,
    ACTIVATION_CODES_LOAD_SUCCESSFULLY
} from "../../api/activation-codes/actions.activationCodes.api";

export function activationCodes(state = [], action) {
    switch (action.type) {
        case ACTIVATION_CODE_GET_SUCCESSFULLY:
            return [action.payload.data];
        case ACTIVATION_CODES_LOAD_SUCCESSFULLY:
            return [...action.payload.data];
        default:
            return state;
    }
}
