import { GDPR_GET_MINIFIED_SUCCESSFULLY } from "../../api/gdpr/actions.gdpr.api";

export function gdpr(state = [], action) {
    switch (action.type) {
        case GDPR_GET_MINIFIED_SUCCESSFULLY:
            return action.payload.data;
        default:
            return state;
    }
}
