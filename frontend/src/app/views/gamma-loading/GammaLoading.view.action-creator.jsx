import {
    GAMMA_LOADING_FINISHED,
    GAMMA_LOADING_START
} from "./DeltaLoading.view.actions";

export function deltaLoadingFinished() {
    return {
        type: GAMMA_LOADING_FINISHED,
        error: false
    };
}

export function deltaLoadingStart() {
    return {
        type: GAMMA_LOADING_START,
        error: false
    };
}
