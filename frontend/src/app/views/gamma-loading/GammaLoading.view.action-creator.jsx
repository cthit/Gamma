import {
    GAMMA_LOADING_FINISHED,
    GAMMA_LOADING_START
} from "./GammaLoading.view.actions";

export function gammaLoadingFinished() {
    return {
        type: GAMMA_LOADING_FINISHED,
        error: false
    };
}

export function gammaLoadingStart() {
    return {
        type: GAMMA_LOADING_START,
        error: false
    };
}
