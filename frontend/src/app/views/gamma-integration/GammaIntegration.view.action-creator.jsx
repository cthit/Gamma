import {
    GAMMA_INTEGRATION_STARTED_FETCHING_ACCESS_TOKEN,
    GAMMA_INTEGRATION_FINISHED_FETCHING_ACCESS_TOKEN
} from "./GammaIntegration.view.actions";

export function startedFetchingAccessToken() {
    return {
        type: GAMMA_INTEGRATION_STARTED_FETCHING_ACCESS_TOKEN,
        error: false
    };
}

export function finishedFetchingAccessToken() {
    return {
        type: GAMMA_INTEGRATION_FINISHED_FETCHING_ACCESS_TOKEN,
        error: false
    };
}
