import {
    GAMMA_INTEGRATION_FINISHED_FETCHING_ACCESS_TOKEN,
    GAMMA_INTEGRATION_STARTED_FETCHING_ACCESS_TOKEN
} from "./GammaIntegration.view.actions";

export function gammaIntegration(
    state = { fetchingAccessToken: false },
    action
) {
    switch (action.type) {
        case GAMMA_INTEGRATION_STARTED_FETCHING_ACCESS_TOKEN:
            return {
                fetchingAccessToken: true
            };
        case GAMMA_INTEGRATION_FINISHED_FETCHING_ACCESS_TOKEN:
            return {
                fetchingAccessToken: false
            };
        default:
            return state;
    }
}
