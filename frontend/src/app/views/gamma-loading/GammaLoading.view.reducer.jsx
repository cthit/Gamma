import {
    GAMMA_LOADING_FINISHED,
    GAMMA_LOADING_START
} from "./DeltaLoading.view.actions";

export function loading(state = true, action) {
    switch (action.type) {
        case GAMMA_LOADING_FINISHED:
            return false;
        case GAMMA_LOADING_START:
            return true;
        default:
            return state;
    }
}
